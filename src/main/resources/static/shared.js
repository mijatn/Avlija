// Shared utilities for Avlija desktop and mobile

function formatNum(val) {
    return parseFloat(val).toFixed(2).replace('.', ',').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}

function fmtDate(dateStr) {
    if (!dateStr) return '';
    const [y, m, d] = dateStr.split('-');
    return `${d}.${m}.${y}`;
}

// ── MODAL ──────────────────────────────────────────────────────
function showModal(message, icon) {
    document.getElementById('modalMessage').textContent = message;
    document.getElementById('modalIcon').textContent = icon || '⚠️';
    document.getElementById('modalOverlay').classList.add('visible');
}
function closeModal() {
    document.getElementById('modalOverlay').classList.remove('visible');
}

// ── CONFIRM MODAL ──────────────────────────────────────────────
let confirmResolve = null;

function showConfirm(message) {
    document.getElementById('confirmMessage').textContent = message;
    document.getElementById('confirmOverlay').classList.add('visible');
    return new Promise(resolve => { confirmResolve = resolve; });
}
function closeConfirm(result) {
    document.getElementById('confirmOverlay').classList.remove('visible');
    if (confirmResolve) { confirmResolve(result); confirmResolve = null; }
}