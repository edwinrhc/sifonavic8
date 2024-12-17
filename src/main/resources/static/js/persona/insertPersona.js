

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

    // **Nueva Verificación: Limitar el Tamaño del Archivo a 10 MB**
    const maxSizeInBytes = 10 * 1024 * 1024; // 10 MB
    if (file.size > maxSizeInBytes) {
        await Swal.fire({
            title: "Error",
            text: "El archivo seleccionado excede el tamaño máximo permitido de 10 MB. Por favor, selecciona un archivo más pequeño.",
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
    console.log('Context Path:', contextPath);

    // const csrfToken = document.querySelector('input[name="_csrf"]').value;
    // const fileInput = document.getElementById("fileInput");
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
    // formData.append("_csrf", csrfToken);

    try {
        const response = await fetch(contextPath+"/api/v1/cargaCSV/cargaInsertDataHedereros", {
            method: "POST",
            body: formData,  // Enviamos el FormData con el archivo
        });

        if (!response.ok) {
            const errorData = await response.json();
            let errorMessage = errorData.message || `Error ${response.status}`;

            // Si existen errores detallados, los agregamos al mensaje
            if (errorData.errors) {
                errorMessage += `\nDetalles:\n${errorData.errors}`;
            }

            throw new Error(errorMessage);
        }

        const data = await response.json();

        // Mostrar mensaje de éxito
        await Swal.fire({
            icon: "success",
            title: "Archivo procesado correctamente",
            text: data.message || "El archivo se procesó con éxito.",
        });

    } catch (error) {
        console.error("Error al procesar el archivo:", error);
        await Swal.fire({
            icon: "error",
            title: "Oops...",
            text: error.message || "Error al procesar el archivo. Por favor, inténtelo nuevamente.",
        });
    }
}




