document.addEventListener('DOMContentLoaded', function () {
    // Deshabilitar el botón de carga inicialmente
    var insertButton = document.getElementById('insertButton');
    insertButton.disabled = true;

    // Evento que se dispara cada vez que se selecciona un archivo
    document.getElementById('file').addEventListener('change', function () {
        var file = this.files[0];
        var maxSize = 5242880; // 1 MB en bytes
        // var maxSize = 1024; // 1 KB en bytes para hacer pruebas

        if (file) {
            if (file.size > maxSize) {
                Swal.fire({
                    title: 'Error',
                    text: 'El archivo es demasiado grande. El tamaño máximo permitido es 1 MB.',
                    icon: 'error',
                    confirmButtonText: 'Aceptar'
                });
                insertButton.disabled = true;
                return;
            } else {
                insertButton.disabled = false; // Habilitar el botón si el tamaño del archivo es correcto
            }
        } else {
            insertButton.disabled = true; // Deshabilitar el botón si no hay archivo seleccionado
        }
    });
});

function validarArchivoCSV() {
    var fileInput = document.getElementById('file');
    var file = fileInput.files[0];

    if (!file) {
        Swal.fire({
            title: 'Error',
            text: 'Por favor, selecciona un archivo CSV antes de intentar cargarlo.',
            icon: 'error',
            confirmButtonText: 'Aceptar'
        });
        return false;
    }

    // Validar la extensión del archivo
    var fileName = file.name;
    if (!fileName.endsWith('.csv')) {
        Swal.fire({
            title: 'Error',
            text: 'El archivo seleccionado no es un archivo CSV. Por favor, selecciona un archivo con extensión .csv.',
            icon: 'error',
            confirmButtonText: 'Aceptar'
        });
        return false;
    }

    var reader = new FileReader();
    var valid = true;

    reader.onload = function (e) {
        var content = e.target.result;
        var lines = content.split('\n');

        if (lines.length <= 1) {
            Swal.fire({
                title: 'Error',
                text: 'El archivo CSV no contiene datos.',
                icon: 'error',
                confirmButtonText: 'Aceptar'
            });
            valid = false;
            return;
        }

        // Validamos los nombres de las columnas
        var header = lines[0].split(';');
        var expectedColumnCount = ["Tip_Doc", "Num_Doc", "Nombres", "App_Paterno", "App_Materno", "Fecha_nacimiento"];
        // var expectedColumnCount = 6;

        if (header.length !== expectedColumnCount.length) {
            Swal.fire({
                title: 'Error',
                text: `El archivo CSV debe tener ${expectedColumnCount.length} columnas.`,
                icon: 'error',
                confirmButtonText: 'Aceptar'
            });
            valid = false;
            return;
        }

        for (var i = 0; i < expectedColumnCount.length; i++) {
            if (header[i].trim() !== expectedColumnCount[i]) {
                Swal.fire({
                    title: 'Error',
                    text: `La columna "${header[i]}" no coincide con la esparada "${expectedColumnCount[i]}".`,
                    icon: 'error',
                    confirmButtonText: 'Aceptar'
                });
                valid = false;
                return;

            }
        }

        // Validar que exista al menos un registro después del encabezado
        var dataLines = lines.slice(1);// Excluir la primera línea que es el encabezado
        var hasData = dataLines.some(line => line.trim() !== '');

        if (!hasData) {
            Swal.fire({
                title: 'Error',
                text: 'El archivo CSV no contiene registros, para actualizar.',
                icon: 'error',
                confirmButtonText: 'Aceptar'
            });
            valid = false;
            return;
        }
    };

    reader.readAsText(file);
    return valid;
}


function validarYSubirArchivo() {
    if (validarArchivoCSV()) {
        uploadFile();  // Llamar la función de carga de archivos si la validación es exitosa
    }
}


function uploadFile() {

    Swal.fire({
        title: '¿Estás seguro?',
        text: "¿Deseas cargar y procesar este archivo?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, cargar',
        cancelButtonText: 'No, cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: 'Cargando...',
                text: 'Por favor espera mientras se carga el archivo',
                didOpen: () => {
                    Swal.showLoading();
                },
                allowOutsideClick: false,
                allowEscapeKey: false,
                allowEnterKey: false
            });

            var formData = new FormData(document.forms[0]);
            fetch('/api/v1/cargaCSV/cargaInsertDataHedereros', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.status === 401) {
                        // Manejar el error 401 (No autorizado)
                        Swal.fire({
                            icon: 'error',
                            title: 'No autorizado',
                            text: 'No tienes permisos para realizar esta acción. Por favor, verifica tus credenciales.',
                        });
                        return;
                    }

                    // Verificar si la respuesta es JSON
                    return response.json();
                })
                .then(response => {
                    if (response && response.fileName) {
                        Swal.fire({
                            icon: response.message.includes('Errores') ? 'error' : 'success',
                            title: response.message.includes('Errores') ? 'Errores encontrados' : 'Archivo procesado correctamente',
                            text: response.message,
                            showCancelButton: true,
                            confirmButtonText: 'Descargar archivo',
                            cancelButtonText: 'Cerrar'
                        }).then((result) => {
                            if (result.isConfirmed) {
                                // Descargar el archivo de errores o resumen
                                window.location.href = 'descargarArchivo?fileName=' + encodeURIComponent(response.fileName);
                            }
                        });
                    } else if (response) {
                        Swal.fire({
                            icon: 'error',
                            title: 'Oops...',
                            text: 'Algo salió mal. Por favor, inténtelo nuevamente.',
                        });
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: 'Error al procesar el archivo. Por favor, inténtelo nuevamente.',
                    });
                });
        }
    });
}