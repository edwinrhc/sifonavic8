function validarCampos() {
    var tipoDoc = document.getElementById("tipoDocumento").value;
    var numDoc = document.getElementById("numDocumento").value;

    // Obtener los valores de los campos
    var tipoDocValue = tipoDoc.trim();
    var numDocValue = numDoc.trim();

    // Verificar si ambos campos tienen un valor
    if (tipoDocValue !== "" && numDocValue !== "") {
        // Si ambos campos tienen un valor, habilitar el botón y restablecer el estilo
        document.getElementById("validarBoton").disabled = false;
        document.getElementById("validarBoton").style.backgroundColor = ""; // Restablecer el color de fondo
        document.getElementById("validarBoton").style.color = ""; // Restablecer el color del texto
        document.getElementById("validarBoton").style.cursor = ""; // Restablecer el cursor
    } else {
        // Si al menos uno de los campos está vacío, desactivar el botón y aplicar el estilo de bloqueado
        document.getElementById("validarBoton").disabled = true;
        document.getElementById("validarBoton").style.backgroundColor = "gray"; // Cambiar el color de fondo a gris
        document.getElementById("validarBoton").style.color = "white"; // Cambiar el color del texto a blanco
        document.getElementById("validarBoton").style.cursor = "not-allowed"; // Cambiar el cursor a no permitido
    }
}

function limpiarInputYValidarCampos() {
    // Limpia el campo de texto
    document.getElementById("numDocumento").value = "";

    // Llama a la función de validación
    validarCampos();
}


$(document).ready(function () {
    console.log('Context Path:', contextPath);
    $('#resultsTable').addClass('hidden');

    // Mapeo de tipo de documento
    const tipoDocumentoMap = {
        1: "DNI",
        2: "Carnet de Extranjería",
        3: "Libreta Electoral (07 dígitos)",
        4: "Libreta Electoral (08 dígitos)",
        5: "Carnet de Fuerzas Policiales",
        6: "Carnet de Fuerzas Armadas",
        10: "Libreta Tributaria",
        15: "Pasaporte"
    };

    const estadoFallecidoMap = {
        0: 'SIN VALIDAR',
        1: 'SIN RESTRICCIÓN',
        2: 'CON RESTRICCIÓN',
        3: 'FALLECIMIENTO',
        5: 'VALIDADO MIGRACIONES',
        6: 'CANCELADO'
    };

    const sexoMap = {
        19: "M",
        20: "F"
    };

    // Función para formatear fechas en dd/mm/aaaa
    function formatDate(fecha) {
        if (!fecha) return "Sin datos";
        const date = new Date(fecha);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
    }

    $('#searchForm').on('submit', function (event) {
        event.preventDefault(); // Evita que el formulario recargue la página

        // Obtén los valores del formulario
        const tipoDocumento = $('#tipoDocumento').val();
        const numDocumento = $('#numDocumento').val();

        // Realiza la solicitud AJAX al backend
        $.ajax({
            url: contextPath + '/api/v1/cargaCSV/search', // Usa contextPath aquí
            type: 'GET',
            data: { tipoDocumento: tipoDocumento, numDocumento: numDocumento },
            success: function (data) {
                // Limpia la tabla antes de actualizarla
                $('#resultsTableBody').empty();

                if(data.consultaPersona && data.consultaPersona.length > 0) {
                    // Si hay resultados, muestra la tabla y oculta el mensaje
                    $('#resultsTable').removeClass('hidden');
                    $('#noResultsMessage').addClass('hidden');

                    // Itera sobre los resultados y crea filas dinámicas
                    data.consultaPersona.forEach(persona => {
                        const row = `
                            <tr>
                                <td class="border border-gray-300 px-4 py-2">${tipoDocumentoMap[persona.tipoDocumento]}</td>
                                <td class="border border-gray-300 px-4 py-2">${persona.numDocumento}</td>
                                <td class="border border-gray-300 px-4 py-2">${persona.apePaterno}</td>
                                <td class="border border-gray-300 px-4 py-2">${persona.apeMaterno}</td>
                                <td class="border border-gray-300 px-4 py-2">${persona.nombres}</td>
                                <td class="border border-gray-300 px-4 py-2">${formatDate(persona.fechaNacimiento)}</td>
                                <td class="border border-gray-300 px-4 py-2">${estadoFallecidoMap[persona.estadoFallecido]}</td>
                                <td class="border border-gray-300 px-4 py-2">${persona.fechaFallecido ? formatDate(persona.fechaFallecido) : 'Sin datos'}</td>
                            </tr>
                        `;
                        $('#resultsTableBody').append(row);
                    });
                } else {
                    // Si no hay resultados, muestra el mensaje y oculta la tabla
                    $('#resultsTable').addClass('hidden');
                    $('#noResultsMessage').removeClass('hidden')
                }
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
                alert('Ocurrió un error al realizar la consulta.');
            }
        });
    });
});
