<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting numberFormat="0.##" />

<html lang="en">
<head><title>Error Occurred</title></head>
<body>
    <h1>Error Occurred!</h1>    
    <b>status: ${status}</b>
    <b>error: ${error}</b>
    <p>message: ${message}</p>
</body>
</html>

