document.addEventListener('DOMContentLoaded', function() {
    // Agregar al carrito
    document.querySelectorAll('.agregar-carrito').forEach(button => {
        button.addEventListener('click', function() {
            const productoId = this.getAttribute('data-producto-id');
            const cantidad = 1; // Por defecto

            fetch('/carrito/agregar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ productoId: productoId, cantidad: cantidad })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Producto agregado al carrito');
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al agregar al carrito');
            });
        });
    });

    // Actualizar cantidad
    document.querySelectorAll('.actualizar-cantidad').forEach(button => {
        button.addEventListener('click', function() {
            const itemId = this.getAttribute('data-item-id');
            const cantidad = document.querySelector('#cantidad-' + itemId).value;

            fetch('/carrito/actualizar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ itemId: itemId, cantidad: cantidad })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload(); // Recargar para actualizar totales
                } else {
                    alert('Error: ' + data.message);
                }
            });
        });
    });

    // Eliminar del carrito
    document.querySelectorAll('.eliminar-item').forEach(button => {
        button.addEventListener('click', function() {
            const itemId = this.getAttribute('data-item-id');

            fetch('/carrito/eliminar/' + itemId, {
                method: 'DELETE'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload();
                } else {
                    alert('Error: ' + data.message);
                }
            });
        });
    });
});