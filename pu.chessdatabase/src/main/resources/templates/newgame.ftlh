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
			<td><label for="wk">Witte Koning</label></td>	
			<td><input id="wk" type="text" name="wk" value="${wk}" /></td>
		</tr>
		<tr>
			<td><label for="witteDame">Witte Dame</label></td>
			<td><input id="s3" type="text" name="s3" value="${s3}" /></td>
		</tr>
		<tr>
			<td><label for="zwarteKoning">Zwarte Koning</label></td>
			<td><input id="zk" type="text" name="zk" value="${zk}" /></td>
		</tr>
		<tr>
			<td><label for="zwarteToren">Zwarte Toren</label></td>
			<td><input id="s4" type="text" name="s4" value="${s4}" /></td>
		</tr>
		<tr>
			<td>Wie is er aan zet</td
			<td
				<label>
					<input type="radio" id="wit" name="aanZet" value="wit" <#if aanZet == "wit">checked</#if> >Wit</input>
				</label>
			</td>
		</tr>
		<tr>
			<td />
			<td>
				<label>
					<input type="radio" id="zwart" name="aanZet" value="zwart" <#if aanZet == "zwart">checked</#if> >Zwart</input>
				</label>
			</td>
		</tr>
	</table>
	<button type="submit" name="submit">Doen</button>
			

	</form>

</body>
</html>
