<#import "/spring.ftl" as spring />
<#setting numberFormat="0.##" />

<head>
	<title>Peter's Chessdatabase</title>
	<link rel=StyleSheet href="<@spring.url '/css/chess.css' />" type="text/css">
	<link rel=StyleSheet href="<@spring.url '/css/main.css' />" type="text/css">
</head>
<body>
		<h1>Peter's Chessdatabase</h1>
    <table class="chess-board">
        <tbody>
            <tr>
                <th></th>
                <th>a</th>
                <th>b</th>
                <th>c</th>
                <th>d</th>
                <th>e</th>
                <th>f</th>
                <th>g</th>
                <th>h</th>
            </tr>
            <tr>
                <th>8</th>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
            </tr>
            <tr>
                <th>7</th>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
            </tr>
            <tr>
                <th>6</th>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
            </tr>
            <tr>
                <th>5</th>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
            </tr>
            <tr>
                <th>4</th>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
            </tr>
            <tr>
                <th>3</th>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
            </tr>
            <tr>
                <th>2</th>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
            </tr>
            <tr>
                <th>1</th>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
                <td class="dark"></td>
                <td class="light"></td>
            </tr>
        </tbody>
    </table>

    <p>${partijDocument.resultaat.res1}, ${partijDocument.resultaat.res2}</p>
    <table class="partij-verloop">
     	<thead>
    		<tr>
    			<th>PartijVerloop</th>
    		</tr>
    	</thead>
			<#list partijDocument.zetten as zet>
				<tr class=${zet?itemParity}>
					<td class="string">${zet}</td>
				</tr>
			</#list>
		</table

		<p>Mogelijke zetten<p>
    <table class="mogelijke-zetten">
    	<thead>
    		<tr>
    			<th class="string">Zetnr</th>
    			<th class="string">Zet</th>
    			<th class="string">Resultaat</th>
    			<th class="string">Mat in hoeveel</th>
    		</tr>
    	</thead>
			<#list partijDocument.gegenereerdeZetten as zet>
				<tr class=${zet?itemParity}>
					<td class="string">${zet.zetNummer}</td>
					<td class="string">${zet.zet}</td>
					<td class="string">${zet.resultaat}</td>
					<td class="string">${zet.matInHoeveel}</td>
				</tr>
			</#list>
		</table

</body>
