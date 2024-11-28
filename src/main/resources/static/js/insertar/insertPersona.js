
/*document.addEventListener('DOMContentLoaded', function () {

    const form = document.getElementById('uploadForm');
    form.addEventListener('submit', validarYSubirArchivo);

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
});*/

/*
function validarYSubirArchivo() {
    const form = document.forms[0]; // Seleccionar el primer formulario
    const formData = new FormData(form); // Crear un FormData a partir del formulario
    const csrfToken = document.querySelector('input[name="_csrf"]').value; // Obtener el CSRF token

    // Confirmación antes de enviar
    Swal.fire({
        title: '¿Estás seguro?',
        text: '¿Deseas cargar y procesar este archivo?',
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

            // Hacer la solicitud con fetch
            fetch('/api/v1/cargaCSV/cargaInsertDataHedereros', {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': csrfToken, // Incluyendo el token CSRF
                    'Accept': 'application/json'
                },
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Error ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(response => {
                    Swal.fire({
                        icon: 'success',
                        title: 'Archivo procesado correctamente',
                        text: response.message
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: 'Error al procesar el archivo. Por favor, inténtelo nuevamente.'
                    });
                });
        }
    });
}
*/


/*function validarYSubirArchivo(event) {

    event.preventDefault(); // Evita el comportamiento predeterminado del formulario
    console.log("Formulario interceptado"); // Para verificar si se ejecuta la función

    const form = document.forms[0]; // Seleccionar el primer formulario
    const formData = new FormData(form); // Crear un FormData a partir del formulario
    const csrfToken = document.querySelector('input[name="_csrf"]').value; // Obtener el CSRF token

    // Confirmación antes de enviar
    Swal.fire({
        title: '¿Estás seguro?',
        text: '¿Deseas cargar y procesar este archivo?',
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

            // Hacer la solicitud con fetch
            fetch('/api/v1/cargaInsertDataHedereros', {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': csrfToken, // Incluyendo el token CSRF
                    // 'Accept': 'application/json'
                },
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Error ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(response => {
                    Swal.fire({
                        icon: 'success',
                        title: 'Archivo procesado correctamente',
                        html: `
                            <p>${response.message}</p>
                            <a href="${response.fileUrl}" download="${response.fileName}" class="btn btn-primary">Descargar archivo</a>
                        `
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: 'Error al procesar el archivo. Por favor, inténtelo nuevamente.'
                    });
                });
        }
    });
}*/


/*
function validarYSubirArchivo() {
    const fileInput = document.getElementById("fileInput");
    const file = fileInput.files[0]; // Obtiene el archivo seleccionado

    // Validar que se haya seleccionado un archivo
    if (!file) {
        Swal.fire({
            icon: "warning",
            title: "Archivo requerido",
            text: "Por favor, selecciona un archivo antes de continuar.",
        });
        return; // Detiene la ejecución si no hay archivo
    }

    const form = document.getElementById("uploadForm");
    const formData = new FormData(form); // Crea un FormData con los datos del formulario
    const csrfToken = document.querySelector('input[name="_csrf"]').value; // Obtiene el token CSRF

    Swal.fire({
        title: "¿Estás seguro?",
        text: "¿Deseas cargar y procesar este archivo?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Sí, cargar",
        cancelButtonText: "No, cancelar",
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: "Cargando...",
                text: "Por favor espera mientras se carga el archivo",
                didOpen: () => Swal.showLoading(),
                allowOutsideClick: false,
            });

            fetch("/api/v1/cargaCSV/cargaInsertDataHedereros", {
                method: "POST",
                headers: {
                    "X-CSRF-TOKEN": csrfToken, // Incluye el token CSRF
                },
                body: formData,
            })
                .then((response) => {
                    if (!response.ok) {
                        throw new Error(`Error ${response.status}: ${response.statusText}`);
                    }
                    return response.json(); // Convierte la respuesta a JSON
                })
                .then((data) => {
                    Swal.fire({
                        icon: "success",
                        title: "Archivo procesado correctamente",
                        html: `
                            <p>${data.message}</p>
                            <a href="${data.fileUrl}" download="${data.fileName}" class="btn btn-primary">Descargar archivo</a>
                        `,
                    });
                })
                .catch((error) => {
                    console.error("Error:", error);
                    Swal.fire({
                        icon: "error",
                        title: "Oops...",
                        text: "Error al procesar el archivo. Por favor, inténtelo nuevamente.",
                    });
                });
        }
    });
}
*/


async function validarArchivoCSV() {
    const fileInput = document.getElementById("fileInput");
    const file = fileInput.files[0];

    if (!file) {
        await Swal.fire({
            title: "Error",
            text: "Por favor, selecciona un archivo CSV antes de intentar cargarlo.",
            icon: "error",
            confirmButtonText: "Aceptar",
        });
        return false;
    }

    if (!file.name.endsWith(".csv")) {
        await Swal.fire({
            title: "Error",
            text: "El archivo seleccionado no es un archivo CSV. Por favor, selecciona un archivo con extensión .csv.",
            icon: "error",
            confirmButtonText: "Aceptar",
        });
        return false;
    }

    return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onload = async (e) => {
            const content = e.target.result;
            const lines = content.split("\n");
            if (lines.length <= 1) {
                await Swal.fire({
                    title: "Error",
                    text: "El archivo CSV no contiene datos.",
                    icon: "error",
                    confirmButtonText: "Aceptar",
                });
                resolve(false);
                return;
            }

            const header = lines[0].split(";");
            const expectedColumns = [
                "Tip_Doc",
                "Num_Doc",
                "Nombres",
                "App_Paterno",
                "App_Materno",
                "Fecha_nacimiento",
            ];

            if (header.length !== expectedColumns.length) {
                await Swal.fire({
                    title: "Error",
                    text: `El archivo CSV debe tener ${expectedColumns.length} columnas.`,
                    icon: "error",
                    confirmButtonText: "Aceptar",
                });
                resolve(false);
                return;
            }

            for (let i = 0; i < expectedColumns.length; i++) {
                if (header[i].trim() !== expectedColumns[i]) {
                    await Swal.fire({
                        title: "Error",
                        text: `La columna "${header[i]}" no coincide con la esperada "${expectedColumns[i]}".`,
                        icon: "error",
                        confirmButtonText: "Aceptar",
                    });
                    resolve(false);
                    return;
                }
            }

            resolve(true); // Validación exitosa
        };

        reader.readAsText(file);
    });
}

async function validarYSubirArchivo() {
    const esValido = await validarArchivoCSV();
    if (esValido) {
        uploadFile(); // Llama a la función de carga solo si la validación pasa
    }
}

async function uploadFile() {
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const fileInput = document.getElementById("fileInput");
    const file = fileInput.files[0];

    // Comprobamos si el archivo existe
    if (!file) {
        await Swal.fire({
            title: "Error",
            text: "No se ha seleccionado un archivo para cargar.",
            icon: "error",
            confirmButtonText: "Aceptar",
        });
        return;
    }

    // Confirmación de carga
    const confirmResult = await Swal.fire({
        title: "¿Estás seguro?",
        text: "¿Deseas cargar y procesar este archivo?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Sí, cargar",
        cancelButtonText: "No, cancelar",
    });

    if (!confirmResult.isConfirmed) return;

    Swal.fire({
        title: "Cargando...",
        text: "Por favor espera mientras se carga el archivo",
        didOpen: () => Swal.showLoading(),
        allowOutsideClick: false,
    });

    // Creamos el FormData y agregamos el archivo
    const formData = new FormData();
    formData.append("file", file);  // Agregamos el archivo directamente

    // También agregamos el token CSRF si es necesario
    formData.append("_csrf", csrfToken);

    try {
        const response = await fetch("/api/v1/cargaCSV/cargaInsertDataHedereros", {
            method: "POST",
            body: formData,  // Enviamos el FormData con el archivo
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }

        const data = await response.json();

        // Mostrar mensaje de éxito
        await Swal.fire({
            icon: "success",
            title: "Archivo procesado correctamente",
            text: data.message || "El archivo se procesó con éxito.",
        });

        // Verificamos si la URL del archivo está presente
        if (data.fileUrl) {
            // Redirigimos a la URL para forzar la descarga
            window.location.href = data.fileUrl; // Descarga el archivo directamente
        }

    } catch (error) {
        console.error("Error al procesar el archivo:", error);
        await Swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Error al procesar el archivo. Por favor, inténtelo nuevamente.",
        });
    }
}


