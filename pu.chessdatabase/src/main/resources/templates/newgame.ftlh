<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting numberFormat="0.##" />

<head>
	<title>Peter's Chessdatabase</title>
	<link rel=StyleSheet href="<@spring.url '/css/main.css' />" type="text/css">
</head>

<body>
	<h1>Welcome to ${AppName}</h1>
	<form action='<@spring.url "/do-newgame" />' method="post">
		<p>Zet een nieuwe stelling op</p>
		<table id="detabel" class="newGame" border="0">
			<tr>
				<td><label for="wkAlfa">${doc.wkLabel}</label></td>	
				<td><input id="wkAlfa" type="text" name="wkAlfa" value="${doc.wk}" /></td>
			</tr>
			<tr>
				<td><label for="s3Alfa">${doc.s3Label}</label></td>
				<td><input id="s3Alfa" type="text" name="s3Alfa" value="${doc.s3}" /></td>
			</tr>
			<tr>
				<td><label for="zkAlfa">${doc.zkLabel}</label></td>
				<td><input id="zkAlfa" type="text" name="zkAlfa" value="${doc.zk}" /></td>
			</tr>
			<tr>
				<td><label for="s4Alfa">${doc.s4Label}</label></td>
				<td><input id="s4Alfa" type="text" name="s4Alfa" value="${doc.s4}" /></td>
			</tr>
			<tr>
				<td>Wie is er aan zet</td>
				<td
					<label>
						<input type="radio" id="wit" name="aanZet" value="Wit" <#if doc.aanZet == "Wit">checked</#if> >Wit</input>
					</label>
				</td>
			</tr>
			<tr>
				<td />
				<td>
					<label>
						<input type="radio" id="zwart" name="aanZet" value="Zwart" <#if doc.aanZet == "Zwart">checked</#if> >Zwart</input>
					</label>
				</td>
			</tr>

		</table>
		
		<button type="submit" name="submit">Doen</button>
	</form>
	
</body>
</html>
