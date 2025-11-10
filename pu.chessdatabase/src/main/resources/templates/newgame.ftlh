<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting numberFormat="0.##" />

<head>
	<title>Peter's Chessdatabase</title>
	<link rel=StyleSheet href="<@spring.url '/css/main.css' />" type="text/css">
</head>

<body>
	<h1>Welcome to ${AppName}</h1>
	
	<h4>Choose a configuration</h4>
	<form action='<@spring.url "/do-switch-config" />' method="post">
		<table id="nogeentabel" class="newGame" border="0">
<#-- 
			<tr>
				<td>Aantal Stukken</td>
				<td
					<#list doc.aantalStukkenList as aantalStukken>
						<label>
							<input type="radio" id="${aantalStukken}Stukken" name="aantalStukken" value="${aantalStukken}" <#if doc.aantalStukken == aantalStukken>checked</#if> >${aantalStukken}Stukken</input>
						</label>
					</#list>
				</td>
			</tr>
 -->
			<tr>
				<td><label for="config">Configuratie</label></td>	
				<td><select name="config" size="1">
					<#list doc.configList as config>
						<option <#if doc.config == config>selected</#if> >${config}</option>
					</#list>
				</td>
			</tr>
		</table>
		<button type="submit" name="submit">Switch configuration</button>
	</form>

	<h4>Zet een nieuwe stelling op</h4>
	<form action='<@spring.url "/do-newgame" />' method="post">
		<#list doc.fakeStukken as stuk>
			<input id="${stuk.name}" type="hidden" name="${stuk.name}" value="${stuk.veld}" />
		</#list>
		<table id="detabel" class="newGame" border="0">
			<#list doc.realStukken as stuk>
				<tr>
					<td><label for="${stuk.name}">${stuk.label}</label></td>	
					<td><input id="${stuk.name}" type="text" name="${stuk.name}" value="${stuk.veld}" /></td>
				</tr>
			</#list>
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
		
		<button type="submit" name="submit">DoIt</button>
	</form>
	
</body>
</html>
