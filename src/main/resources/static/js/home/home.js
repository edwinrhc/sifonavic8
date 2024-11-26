





// Función para abrir el modal
function openModal() {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modal-title');
    const modalContent = document.getElementById('modal-content');

    // // Cambiar el contenido del modal basado en la solicitud
    // modalTitle.textContent = `Detalles de la Solicitud: ${sol.solDCodigo}`;
    // modalContent.innerHTML = `
    //     <p><strong>Fecha de creación:</strong> ${sol.solFechaCreacion}</p>
    //     <p><strong>Tipo de solicitud:</strong> ${sol.tipoSolicitudes.tipSolDescripcion}</p>
    //     <p><strong>Asignado a:</strong> ${sol.solAsignado}</p>
    //     <p><strong>Descripción:</strong> ${sol.descripcion}</p>
    //     <p><strong>Fecha de asignación:</strong> ${sol.fechaAsignacion}</p>
    //     <p><strong>Otro Campo 1:</strong> ${sol.otroCampo1}</p>
    //     <p><strong>Otro Campo 2:</strong> ${sol.otroCampo2}</p>
    //     <!-- Agrega más campos según sea necesario -->
    // `;
    //
    // modal.classList.remove('hidden');
}

// Función para cerrar el modal
function closeModal() {
    const modal = document.getElementById('modal');
    modal.classList.add('hidden');
}

// Función para mostrar/ocultar el menú
function toggleMenu() {
    var menu = document.getElementById('userMenu');
    menu.classList.toggle('hidden');
}


function toggleSection(sectionId) {
    const section = document.getElementById(sectionId);
    section.classList.toggle('hidden');
}