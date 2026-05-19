function formatNum(val) {
    return Number(val).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function openInvoice(data, date, bills, invoiceNumber) {
    const totalPrice = data.reduce((sum, row) => sum + Number(row.total), 0);
    const now = new Date();
    const time = now.toTimeString().split(' ')[0];

    const rows = data.map(row => `
        <tr>
            <td class="name">${row.articlename}</td>
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
            size: 80mm auto;
            margin: 4mm 0;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Courier New', monospace;
            font-size: 9pt;
            color: #000;
            background: #fff;
            width: 80mm;
            margin: 0 auto;
            padding: 0 3mm;
        }

        .restaurant-name {
            text-align: center;
            font-size: 16pt;
            font-weight: bold;
            letter-spacing: 3px;
            margin: 4px 0 2px 0;
        }

        .restaurant-sub {
            text-align: center;
            font-size: 8pt;
            line-height: 1.4;
        }

        .divider {
            border: none;
            border-top: 1px dashed #000;
            margin: 5px 0;
        }

        .divider-solid {
            border: none;
            border-top: 1px solid #000;
            margin: 5px 0;
        }

        .meta {
            display: flex;
            justify-content: space-between;
            font-size: 8pt;
            line-height: 1.6;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 2px;
            table-layout: fixed;
        }

        th {
            text-align: left;
            padding: 2px 0;
            font-size: 8pt;
            border-bottom: 1px solid #000;
        }

        td {
            padding: 2px 1px;
            font-size: 8pt;
            vertical-align: top;
        }

        td.name { word-wrap: break-word; }

        /* Artikal | Kol | Cena | Ukupno */
        th:first-child,  td:first-child  { width: 40%; }
        th:nth-child(2), td:nth-child(2) { width: 13%; text-align: right; }
        th:nth-child(3), td:nth-child(3) { width: 20%; text-align: right; }
        th:nth-child(4), td:nth-child(4) { width: 27%; text-align: right; }

        th.right, td.right { text-align: right; }

        .total-row {
            display: flex;
            justify-content: space-between;
            font-weight: bold;
            font-size: 11pt;
            margin-top: 4px;
        }

        .footer {
            text-align: center;
            margin-top: 10px;
            font-size: 8pt;
            line-height: 2;
        }

        @media print {
            html, body {
                width: 80mm;
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
                <th class="right">Kol</th>
                <th class="right">Cena</th>
                <th class="right">Ukupno</th>
            </tr>
        </thead>
        <tbody>
            ${rows}
        </tbody>
    </table>
    <hr class="divider-solid">
    <div class="total-row">
        <span>UKUPNO:</span>
        <span>${formatNum(totalPrice)} RSD</span>
    </div>
    <hr class="divider">
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