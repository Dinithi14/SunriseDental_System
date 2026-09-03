/**
 * SUNRISE DENTAL CLINIC - APPLICATION CLIENT JAVASCRIPT
 * Pure Vanilla JavaScript (No Frameworks required)
 */

document.addEventListener('DOMContentLoaded', () => {
    initModals();
    initPatientSelector();
    initBillingCalculator();
    initLiveSearch();
});

/* ==========================================================
   MODAL CONTROLLER
   ========================================================== */
function initModals() {
    // Open modal triggers
    document.querySelectorAll('[data-modal-target]').forEach(trigger => {
        trigger.addEventListener('click', (e) => {
            e.preventDefault();
            const modalId = trigger.getAttribute('data-modal-target');
            openModal(modalId);
        });
    });

    // Close buttons
    document.querySelectorAll('.close-modal, .modal-close').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const modal = btn.closest('.modal');
            if (modal) modal.classList.remove('active');
        });
    });

    // Close on backdrop click
    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            e.target.classList.remove('active');
        }
    });
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

/* ==========================================================
   PATIENT SELECTOR & INLINE REGISTRATION TOGGLE
   ========================================================== */
function initPatientSelector() {
    const patientSelect = document.getElementById('patientSelect');
    const newPatientFields = document.getElementById('newPatientFields');

    if (patientSelect && newPatientFields) {
        patientSelect.addEventListener('change', () => {
            if (patientSelect.value === 'NEW') {
                newPatientFields.style.display = 'block';
                newPatientFields.querySelectorAll('input').forEach(input => input.required = true);
            } else {
                newPatientFields.style.display = 'none';
                newPatientFields.querySelectorAll('input').forEach(input => input.required = false);
            }
        });
    }
}

/* ==========================================================
   BILLING DYNAMIC STRATEGY CALCULATOR
   ========================================================== */
function initBillingCalculator() {
    const apptSelect = document.getElementById('billingApptSelect');
    const strategySelect = document.getElementById('billingStrategySelect');
    const addChargesInput = document.getElementById('additionalCharges');

    if (apptSelect) {
        apptSelect.addEventListener('change', updateBillingPreview);
    }
    if (strategySelect) {
        strategySelect.addEventListener('change', updateBillingPreview);
    }
    if (addChargesInput) {
        addChargesInput.addEventListener('input', updateBillingPreview);
    }
}

function updateBillingPreview() {
    const apptSelect = document.getElementById('billingApptSelect');
    const strategySelect = document.getElementById('billingStrategySelect');
    const addChargesInput = document.getElementById('additionalCharges');

    if (!apptSelect || !apptSelect.value) return;

    const selectedOption = apptSelect.options[apptSelect.selectedIndex];
    const treatmentCost = parseFloat(selectedOption.getAttribute('data-treatment-cost') || 0);
    const consultationFee = parseFloat(selectedOption.getAttribute('data-consultation-fee') || 0);
    const additionalCharges = parseFloat(addChargesInput ? (addChargesInput.value || 0) : 0);
    const strategy = strategySelect ? strategySelect.value : 'STANDARD';

    // Real-time Strategy Calculation in UI
    let discount = 0;
    let strategyLabel = 'Standard Tariff';

    if (strategy === 'SENIOR_DISCOUNT') {
        discount = (treatmentCost + consultationFee + additionalCharges) * 0.10;
        strategyLabel = 'Senior Citizen Concession (10%)';
    } else if (strategy === 'CHILD_DISCOUNT') {
        discount = (treatmentCost + consultationFee + additionalCharges) * 0.15;
        strategyLabel = 'Pediatric / Child Discount (15%)';
    } else if (strategy === 'INSURANCE') {
        discount = (treatmentCost + consultationFee + additionalCharges) * 0.80;
        strategyLabel = 'Dental Insurance Coverage (80% Covered)';
    } else if (strategy === 'EMERGENCY') {
        const surcharge = (treatmentCost + consultationFee + additionalCharges) * 0.20;
        discount = -surcharge; // Negative discount = surcharge
        strategyLabel = 'Emergency Care Surcharge (+20%)';
    }

    const subtotal = treatmentCost + consultationFee + additionalCharges - (discount > 0 ? discount : 0) + (discount < 0 ? Math.abs(discount) : 0);

    // Update DOM UI preview elements
    setText('previewTreatmentCost', 'Rs. ' + treatmentCost.toFixed(2));
    setText('previewConsultationFee', 'Rs. ' + consultationFee.toFixed(2));
    setText('previewAdditionalCharges', 'Rs. ' + additionalCharges.toFixed(2));
    setText('previewDiscount', (discount >= 0 ? '- Rs. ' + discount.toFixed(2) : '+ Rs. ' + Math.abs(discount).toFixed(2)) + ' (' + strategyLabel + ')');
    setText('previewTotal', 'Rs. ' + subtotal.toFixed(2));
}

function setText(elementId, text) {
    const el = document.getElementById(elementId);
    if (el) el.innerText = text;
}

/* ==========================================================
   LIVE SEARCH & DETAILS VIEWER
   ========================================================= */
function initLiveSearch() {
    const searchInput = document.getElementById('searchQuery');
    const statusFilter = document.getElementById('statusFilter');
    const searchBtn = document.getElementById('btnSearch');

    if (searchInput) {
        searchInput.addEventListener('input', debounce(() => performSearch(), 300));
    }
    if (statusFilter) {
        statusFilter.addEventListener('change', () => performSearch());
    }
    if (searchBtn) {
        searchBtn.addEventListener('click', (e) => {
            e.preventDefault();
            performSearch();
        });
    }
}

function performSearch() {
    const query = document.getElementById('searchQuery') ? document.getElementById('searchQuery').value : '';
    const status = document.getElementById('statusFilter') ? document.getElementById('statusFilter').value : 'ALL';
    const resultsContainer = document.getElementById('searchResultsBody');

    if (!resultsContainer) return;

    fetch(`api/appointments?action=search&query=${encodeURIComponent(query)}&status=${encodeURIComponent(status)}`)
        .then(res => res.json())
        .then(data => {
            renderSearchResults(data, resultsContainer);
        })
        .catch(err => {
            console.error('Search error:', err);
        });
}

function renderSearchResults(items, container) {
    if (!items || items.length === 0) {
        container.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 2rem; color: #64748b;">No matching appointments found.</td></tr>`;
        return;
    }

    let html = '';
    items.forEach(item => {
        const badgeClass = item.status ? item.status.toLowerCase() : 'scheduled';
        html += `
            <tr>
                <td><strong>${item.appointmentNumber}</strong></td>
                <td>
                    <div style="font-weight: 600;">${item.patientName}</div>
                    <small style="color: #64748b;">${item.patientContact} | ${item.patientCode}</small>
                </td>
                <td>
                    <div>${item.dentistName}</div>
                    <small style="color: #64748b;">${item.dentistSpecialization}</small>
                </td>
                <td>${item.treatmentName}</td>
                <td>
                    <div>${item.appointmentDate}</div>
                    <small style="color: #0284c7; font-weight: 600;">${item.appointmentTime}</small>
                </td>
                <td><span class="badge badge-${badgeClass}">${item.status}</span></td>
                <td>
                    <div style="display: flex; gap: 0.5rem;">
                        <button class="btn btn-secondary btn-sm" onclick="viewAppointmentDetail('${item.appointmentNumber}')">View Details</button>
                        ${item.status !== 'COMPLETED' ? `<a href="billing?appNo=${item.appointmentNumber}" class="btn btn-primary btn-sm">Bill</a>` : `<a href="billing?action=receipt&appointmentId=${item.appointmentId}" class="btn btn-success btn-sm">Receipt</a>`}
                    </div>
                </td>
            </tr>
        `;
    });
    container.innerHTML = html;
}

function viewAppointmentDetail(appNo) {
    fetch(`api/appointments?action=getDetail&appNo=${encodeURIComponent(appNo)}`)
        .then(res => res.json())
        .then(data => {
            if (!data) {
                alert('Appointment not found!');
                return;
            }

            document.getElementById('modalAppNo').innerText = data.appointmentNumber;
            document.getElementById('modalPatientName').innerText = data.patientName;
            document.getElementById('modalPatientCode').innerText = data.patientCode;
            document.getElementById('modalPatientContact').innerText = data.patientContact;
            document.getElementById('modalPatientAddress').innerText = data.patientAddress;
            document.getElementById('modalDentist').innerText = data.dentistName + ' (' + data.dentistSpecialization + ')';
            document.getElementById('modalRoom').innerText = data.roomNumber;
            document.getElementById('modalTreatment').innerText = data.treatmentName + ' (Rs. ' + parseFloat(data.treatmentCost).toFixed(2) + ')';
            document.getElementById('modalDate').innerText = data.appointmentDate + ' at ' + data.appointmentTime;
            document.getElementById('modalStatus').innerText = data.status;
            document.getElementById('modalNotes').innerText = data.notes || 'None';

            const actionBtnContainer = document.getElementById('modalActionButtons');
            if (actionBtnContainer) {
                actionBtnContainer.innerHTML = `
                    <a href="billing?appNo=${data.appointmentNumber}" class="btn btn-primary">Process Billing & Invoice</a>
                    ${data.billId ? `<a href="billing?action=receipt&billId=${data.billId}" target="_blank" class="btn btn-success">Print Official Receipt</a>` : ''}
                `;
            }

            openModal('appointmentDetailModal');
        })
        .catch(err => {
            console.error('Error fetching detail:', err);
            alert('Error loading appointment details');
        });
}

function printReceipt() {
    window.print();
}

function debounce(func, wait) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}
