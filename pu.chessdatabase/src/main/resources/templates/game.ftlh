<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting numberFormat="0.##" />

<head>
	<title>Peter's Chessdatabase</title>
	<link rel=StyleSheet href="<@spring.url '/css/chess.css' />" type="text/css">
	<link rel=StyleSheet href="<@spring.url '/css/main.css' />" type="text/css">
	<script src="<@spring.url '/js/jquery-3.7.1.js' />" ></script>
	<script src="<@spring.url '/js/script.js' />" ></script>
</head>
<body>
	<div hidden id="wk">${partijDocument.wk}</div>
	<div hidden id="zk">${partijDocument.zk}</div>
	<div hidden id="s3">${partijDocument.s3}</div>
	<div hidden id="s4">${partijDocument.s4}</div>
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
            <td id="70" class="light"></td>
            <td id="71" class="dark"></td>
            <td id="72" class="light"></td>
            <td id="73" class="dark"></td>
            <td id="74" class="light"></td>
            <td id="75" class="dark"></td>
            <td id="76" class="light"></td>
            <td id="77" class="dark"></td>
        </tr>
        <tr>
            <th>7</th>
            <td id="60" class="dark"></td>
            <td id="61" class="light"></td>
            <td id="62" class="dark"></td>
            <td id="63" class="light"></td>
            <td id="64" class="dark"></td>
            <td id="65" class="light"></td>
            <td id="66" class="dark"></td>
            <td id="67" class="light"></td>
        </tr>
        <tr>
            <th>6</th>
            <td id="50" class="light"></td>
            <td id="51" class="dark"></td>
            <td id="52" class="light"></td>
            <td id="53" class="dark"></td>
            <td id="54" class="light"></td>
            <td id="55" class="dark"></td>
            <td id="56" class="light"></td>
            <td id="57" class="dark"></td>
        </tr>
        <tr>
            <th>5</th>
            <td id="40" class="dark"></td>
            <td id="41" class="light"></td>
            <td id="42" class="dark"></td>
            <td id="43" class="light"></td>
            <td id="44" class="dark"></td>
            <td id="45" class="light"></td>
            <td id="46" class="dark"></td>
            <td id="47" class="light"></td>
        </tr>
        <tr>
            <th>4</th>
            <td id="30" class="light"></td>
            <td id="31" class="dark"></td>
            <td id="32" class="light"></td>
            <td id="33" class="dark"></td>
            <td id="34" class="light"></td>
            <td id="35" class="dark"></td>
            <td id="36" class="light"></td>
            <td id="37" class="dark"></td>
        </tr>
        <tr>
            <th>3</th>
            <td id="20" class="dark"></td>
            <td id="21" class="light"></td>
            <td id="22" class="dark"></td>
            <td id="23" class="light"></td>
            <td id="24" class="dark"></td>
            <td id="25" class="light"></td>
            <td id="26" class="dark"></td>
            <td id="27" class="light"></td>
        </tr>
        <tr>
            <th>2</th>
            <td id="10" class="light"></td>
            <td id="11" class="dark"></td>
            <td id="12" class="light"></td>
            <td id="13" class="dark"></td>
            <td id="14" class="light"></td>
            <td id="15" class="dark"></td>
            <td id="16" class="light"></td>
            <td id="17" class="dark"></td>
        </tr>
        <tr>
            <th>1</th>
            <td id="0" class="dark"></td>
            <td id="1" class="light"></td>
            <td id="2" class="dark"></td>
            <td id="3" class="light"></td>
            <td id="4" class="dark"></td>
            <td id="5" class="light"></td>
            <td id="6" class="dark"></td>
            <td id="7" class="light"></td>
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
