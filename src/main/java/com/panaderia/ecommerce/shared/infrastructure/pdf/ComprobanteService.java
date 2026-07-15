package com.panaderia.ecommerce.shared.infrastructure.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComprobanteService {

    @Value("${comprobante.upload.dir:comprobantes}")
    private String uploadDir;

    private final JdbcTemplate jdbcTemplate;
    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");
    private static final String EMPRESA_NOMBRE = "Panaderia y Pasteleria Barrios";
    private static final String EMPRESA_RUC = "10004847356";
    private static final String EMPRESA_DIRECCION = "Calle 8 de septiembre 2127, TACNA";
    private static final String EMPRESA_TELEFONO = "+51 988 954 525";

    public ComprobanteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public byte[] generarComprobante(Long pedidoId, String tipoComprobante) throws IOException {
        Optional<Map<String, Object>> pedidoOpt = loadPedidoCompleto(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido no encontrado");
        }

        Map<String, Object> pedido = pedidoOpt.get();
        
        // Crear carpeta con permisos
        File pdfDir = new File(uploadDir);
        if (!pdfDir.exists()) {
            if (!pdfDir.mkdirs()) {
                throw new IOException("No se pudo crear la carpeta: " + uploadDir);
            }
        }
        if (!pdfDir.canWrite()) {
            throw new IOException("No hay permisos de escritura en: " + uploadDir);
        }

        String numeroComprobante = generarNumeroComprobante(tipoComprobante);
        String fileName = tipoComprobante.toLowerCase() + "_" + numeroComprobante + ".pdf";
        File file = new File(pdfDir, fileName);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Fuentes
        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();
        PdfFont boldFont = PdfFontFactory.createFont();

        // Encabezado
        document.add(new Paragraph(EMPRESA_NOMBRE)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("RUC: " + EMPRESA_RUC)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(EMPRESA_DIRECCION)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(EMPRESA_TELEFONO)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Tipo de comprobante
        String tituloComprobante = tipoComprobante.equals("FACTURA") ? "FACTURA ELECTRÓNICA" : "BOLETA DE VENTA";
        document.add(new Paragraph(tituloComprobante)
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Número: " + numeroComprobante)
                .setFontSize(12)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Datos del cliente
        document.add(new Paragraph("DATOS DEL CLIENTE")
                .setFontSize(11)
                .setBold()
                .setMarginTop(10));

        Table clientTable = new Table(2);
        clientTable.setWidth(500);
        clientTable.addCell(new Cell().add(new Paragraph("Nombre/Razón Social:")).setBold());
        clientTable.addCell(new Cell().add(new Paragraph(
                (String) pedido.getOrDefault("clienteNombre", "") + " " + 
                (String) pedido.getOrDefault("clienteApellidos", ""))));
        
        String razonSocial = (String) pedido.get("razonSocial");
        if (razonSocial != null && !razonSocial.isEmpty()) {
            clientTable.addCell(new Cell().add(new Paragraph("Empresa:")).setBold());
            clientTable.addCell(new Cell().add(new Paragraph(razonSocial)));
        }

        String ruc = (String) pedido.getOrDefault("clienteRuc", "");
        if (!ruc.isEmpty()) {
            clientTable.addCell(new Cell().add(new Paragraph("RUC/DNI:")).setBold());
            clientTable.addCell(new Cell().add(new Paragraph(ruc)));
        }

        clientTable.addCell(new Cell().add(new Paragraph("Teléfono:")).setBold());
        clientTable.addCell(new Cell().add(new Paragraph((String) pedido.getOrDefault("clienteTelefono", ""))));

        document.add(clientTable);
        document.add(new Paragraph("").setMarginBottom(10));

        // Datos de la orden
        document.add(new Paragraph("DATOS DEL PEDIDO")
                .setFontSize(11)
                .setBold()
                .setMarginTop(10));

        Table orderTable = new Table(2);
        orderTable.setWidth(500);
        orderTable.addCell(new Cell().add(new Paragraph("Número de Pedido:")).setBold());
        orderTable.addCell(new Cell().add(new Paragraph("#" + pedido.get("idPedido"))));

        orderTable.addCell(new Cell().add(new Paragraph("Fecha:")).setBold());
        orderTable.addCell(new Cell().add(new Paragraph(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))));

        orderTable.addCell(new Cell().add(new Paragraph("Tipo de Entrega:")).setBold());
        orderTable.addCell(new Cell().add(new Paragraph((String) pedido.getOrDefault("tipoEntrega", ""))));

        orderTable.addCell(new Cell().add(new Paragraph("Fecha Entrega:")).setBold());
        orderTable.addCell(new Cell().add(new Paragraph((String) pedido.getOrDefault("fechaEntrega", "-"))));

        document.add(orderTable);
        document.add(new Paragraph("").setMarginBottom(10));

        // Dirección de entrega
        String tipoEntrega = (String) pedido.get("tipoEntrega");
        if ("DOMICILIO".equals(tipoEntrega)) {
            document.add(new Paragraph("DIRECCIÓN DE ENTREGA")
                    .setFontSize(11)
                    .setBold()
                    .setMarginTop(10));

            Table dirTable = new Table(1);
            dirTable.setWidth(500);
            String direccion = (String) pedido.getOrDefault("calle", "") + " #" + 
                             (String) pedido.getOrDefault("numero", "");
            dirTable.addCell(new Cell().add(new Paragraph(direccion)));
            
            String referencia = (String) pedido.get("referencia");
            if (referencia != null && !referencia.isEmpty()) {
                dirTable.addCell(new Cell().add(new Paragraph("Referencia: " + referencia)));
            }
            
            dirTable.addCell(new Cell().add(new Paragraph("Distrito: " + 
                    pedido.getOrDefault("distritoNombre", ""))));

            document.add(dirTable);
            document.add(new Paragraph("").setMarginBottom(10));
        }

        // Detalle de productos
        document.add(new Paragraph("DETALLE DE PRODUCTOS")
                .setFontSize(11)
                .setBold()
                .setMarginTop(10));

        Table productTable = new Table(4);
        productTable.setWidth(500);
        productTable.addCell(new Cell().add(new Paragraph("Descripción")).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
        productTable.addCell(new Cell().add(new Paragraph("Cantidad")).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
        productTable.addCell(new Cell().add(new Paragraph("P. Unitario")).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
        productTable.addCell(new Cell().add(new Paragraph("Subtotal")).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));

        var items = loadPedidoItems(pedidoId);
        for (Map<String, Object> item : items) {
            productTable.addCell(new Cell().add(new Paragraph((String) item.get("nombre"))));
            productTable.addCell(new Cell().add(new Paragraph(item.get("cantidad").toString())));
            productTable.addCell(new Cell().add(new Paragraph("S/ " + item.get("precioUnitario"))));
            productTable.addCell(new Cell().add(new Paragraph("S/ " + item.get("subtotal"))));
        }

        document.add(productTable);
        document.add(new Paragraph("").setMarginBottom(10));

        // Totales
        BigDecimal subtotal = (BigDecimal) pedido.get("subtotalProductos");
        BigDecimal costoEnvio = (BigDecimal) pedido.getOrDefault("costoEnvio", BigDecimal.ZERO);
        BigDecimal igv = subtotal.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalConIGV = subtotal.add(igv).add(costoEnvio).setScale(2, RoundingMode.HALF_UP);

        Table totalsTable = new Table(2);
        totalsTable.setWidth(300);
        totalsTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);

        totalsTable.addCell(new Cell().add(new Paragraph("Subtotal:")).setBold());
        totalsTable.addCell(new Cell().add(new Paragraph("S/ " + subtotal)));

        totalsTable.addCell(new Cell().add(new Paragraph("IGV (18%):")).setBold());
        totalsTable.addCell(new Cell().add(new Paragraph("S/ " + igv)));

        if (costoEnvio.compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(new Cell().add(new Paragraph("Costo Envío:")).setBold());
            totalsTable.addCell(new Cell().add(new Paragraph("S/ " + costoEnvio)));
        }

        totalsTable.addCell(new Cell()
                .add(new Paragraph("TOTAL:"))
                .setBold()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        totalsTable.addCell(new Cell()
                .add(new Paragraph("S/ " + totalConIGV))
                .setBold()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));

        document.add(totalsTable);

        // Footer
        document.add(new Paragraph("")
                .setMarginTop(20));
        document.add(new Paragraph("Gracias por su compra")
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic());
        document.add(new Paragraph("Este documento es válido como comprobante fiscal")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(9));

        document.close();

        // Guardar referencia en BD
        saveComprobanteReference(pedidoId, numeroComprobante, tipoComprobante, fileName);

        return java.nio.file.Files.readAllBytes(file.toPath());
    }

    private String generarNumeroComprobante(String tipoComprobante) {
        String prefijo = tipoComprobante.equals("FACTURA") ? "F" : "B";
        return prefijo + "-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void saveComprobanteReference(Long pedidoId, String numeroComprobante, 
                                         String tipoComprobante, String archivo) {
        String sql = "INSERT INTO comprobante (id_pedido, numero_comprobante, tipo_comprobante, archivo_path) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE numero_comprobante = ?, archivo_path = ?, fecha_generacion = CURRENT_TIMESTAMP";
        
        try {
            jdbcTemplate.update(sql, pedidoId, numeroComprobante, tipoComprobante, archivo,
                    numeroComprobante, archivo);
        } catch (Exception e) {
            // Log error pero no bloquear la generación del PDF
            System.err.println("Error saving comprobante reference: " + e.getMessage());
        }
    }

    private Optional<Map<String, Object>> loadPedidoCompleto(Long pedidoId) {
        try {
            return jdbcTemplate.query(
                    "SELECT p.id_pedido, p.fecha_registro, p.fecha_entrega, p.tipo_entrega, p.estado, " +
                    "p.subtotal_productos, p.costo_envio, p.costo_total, p.tipo_comprobante, " +
                    "c.nombre AS cliente_nombre, c.apellidos AS cliente_apellidos, c.email AS cliente_email, " +
                    "c.telefono AS cliente_telefono, c.razon_social, c.ruc AS cliente_ruc, " +
                    "d.calle, d.numero, d.referencia, dist.nombre AS distrito_nombre " +
                    "FROM pedido p " +
                    "LEFT JOIN cliente c ON p.id_cliente = c.id_cliente " +
                    "LEFT JOIN direccion d ON p.id_direccion_entrega = d.id_direccion " +
                    "LEFT JOIN distrito dist ON d.id_distrito = dist.id_distrito " +
                    "WHERE p.id_pedido = ?",
                    new Object[]{pedidoId},
                    rs -> {
                        if (rs.next()) {
                            Map<String, Object> map = new java.util.HashMap<>();
                            map.put("idPedido", rs.getInt("id_pedido"));
                            map.put("clienteNombre", rs.getString("cliente_nombre"));
                            map.put("clienteApellidos", rs.getString("cliente_apellidos"));
                            map.put("clienteEmail", rs.getString("cliente_email"));
                            map.put("clienteTelefono", rs.getString("cliente_telefono"));
                            map.put("razonSocial", rs.getString("razon_social"));
                            map.put("clienteRuc", rs.getString("cliente_ruc"));
                            java.sql.Date fechaSQL = rs.getDate("fecha_entrega");
                            map.put("fechaEntrega", fechaSQL != null ? fechaSQL.toString() : "-");
                            map.put("tipoEntrega", rs.getString("tipo_entrega"));
                            map.put("estado", rs.getString("estado"));
                            map.put("subtotalProductos", rs.getBigDecimal("subtotal_productos"));
                            map.put("costoEnvio", rs.getBigDecimal("costo_envio"));
                            map.put("costoTotal", rs.getBigDecimal("costo_total"));
                            map.put("calle", rs.getString("calle"));
                            map.put("numero", rs.getString("numero"));
                            map.put("referencia", rs.getString("referencia"));
                            map.put("distritoNombre", rs.getString("distrito_nombre"));
                            return Optional.of(map);
                        }
                        return Optional.empty();
                    });
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private java.util.List<Map<String, Object>> loadPedidoItems(Long pedidoId) {
        try {
            return jdbcTemplate.query(
                    "SELECT p.nombre, pp.cantidad, pp.precio_unitario_congelado, pp.subtotal " +
                    "FROM pedido_producto pp " +
                    "JOIN producto p ON pp.id_producto = p.id_producto " +
                    "WHERE pp.id_pedido = ?",
                    new Object[]{pedidoId},
                    (rs, rowNum) -> {
                        Map<String, Object> map = new java.util.HashMap<>();
                        map.put("nombre", rs.getString("nombre"));
                        map.put("cantidad", rs.getInt("cantidad"));
                        map.put("precioUnitario", rs.getBigDecimal("precio_unitario_congelado"));
                        map.put("subtotal", rs.getBigDecimal("subtotal"));
                        return map;
                    });
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    public Optional<String> obtenerComprobanteGenerado(Long pedidoId) {
        try {
            String sql = "SELECT archivo_path FROM comprobante WHERE id_pedido = ?";
            String resultado = jdbcTemplate.queryForObject(sql, new Object[]{pedidoId}, String.class);
            return Optional.ofNullable(resultado);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
