function formatNum(val) {
    return Number(val).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function openInvoice(data, date, bills, invoiceNumber) {
    const totalPrice = data.reduce((sum, row) => sum + Number(row.total), 0);
    const now = new Date();
    const time = now.toTimeString().split(' ')[0];

    const rows = data.map(row => `
        <tr>
            <td>${row.articlename}</td>
            <td class="right">${formatNum(row.total_qty)}</td>
            <td class="right">${formatNum(row.total_price)}</td>
            <td class="right">${formatNum(row.total)}</td>
        </tr>
    `).join('');

    const html = `
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Račun - Avlija</title>
    <style>
        @page {
            size: A4 portrait;
            margin: 20mm 8mm 20mm 8mm;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Courier New', monospace;
            font-size: 12pt;
            color: #000;
            background: #fff;
            width: 170mm;
            margin: 0 auto;
        }

        .restaurant-name {
            text-align: center;
            font-size: 22pt;
            font-weight: bold;
            letter-spacing: 4px;
            margin-top: 3em;
            margin-bottom: 4px;
        }

        .restaurant-sub {
            text-align: center;
            font-size: 11pt;
            margin-bottom: 2px;
        }

        .divider {
            border: none;
            border-top: 1px dashed #000;
            margin: 10px 0;
        }

        .meta {
            display: flex;
            justify-content: space-between;
            font-size: 11pt;
            margin-bottom: 4px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 6px;
            table-layout: fixed;
        }

        th {
            text-align: left;
            border-bottom: 1px solid #000;
            padding: 4px 0;
            font-size: 11pt;
        }

        td {
            padding: 4px 2px;
            font-size: 11pt;
            word-wrap: break-word;
        }

        td:first-child, th:first-child { width: 47%; }
        td:nth-child(2), th:nth-child(2) { width: 12%; text-align: right; }
        td:nth-child(3), th:nth-child(3) { width: 12%; text-align: right; }
        td:nth-child(4), th:nth-child(4) { width: 29%; text-align: right; }

        th.right, td.right { text-align: right; }

        .total-row {
            display: flex;
            justify-content: space-between;
            font-weight: bold;
            font-size: 13pt;
            margin-top: 6px;
        }

        .footer {
            text-align: center;
            margin-top: 20px;
            font-size: 11pt;
            line-height: 2;
        }

        @media print {
            html, body {
                width: 170mm;
                margin: 0 auto;
            }
        }
    </style>
</head>
<body>
    <div class="restaurant-name">AVLIJA</div>
    <div class="restaurant-sub">Kafana</div>
    <div class="restaurant-sub">Guča, Srbija</div>
    <hr class="divider">
    <div class="meta"><span>Datum:</span><span>${date}</span></div>
    <div class="meta"><span>Vreme:</span><span>${time}</span></div>
    ${invoiceNumber ? `<div class="meta"><span>Broj računa:</span><span>${invoiceNumber}</span></div>` : ''}
    <hr class="divider">
    <table>
        <thead>
            <tr>
                <th>Artikal</th>
                <th class="right">Kol.</th>
                <th class="right">Cena</th>
                <th class="right">Ukupno</th>
            </tr>
        </thead>
        <tbody>
            ${rows}
        </tbody>
    </table>
    <hr class="divider">
    <div class="total-row">
        <span>UKUPNO:</span>
        <span>${formatNum(totalPrice)}</span>
    </div>
    <div class="footer">
        Hvala na poseti!<br>
        ★ Dodjite nam ponovo ★
    </div>
    <script>
        window.onload = function() { window.print(); }
    <\/script>
</body>
</html>`;

    const win = window.open('', '_blank');
    win.document.write(html);
    win.document.close();
}