<?xml version="1.0" encoding="UTF-8"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
		  xmlns:c="http://java.sun.com/jsp/jstl/core"
		  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
		  version="2.1">

    <jsp:directive.page contentType="text/html; charset=utf-8" />
    <jsp:output doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
				doctype-root-element="html" omit-xml-declaration="false"/>
	<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			<meta http-equiv="Content-Language" content="en" />
			<meta name="Description" content="Simple chat with ontology." />

			<noscript>
				<meta http-equiv="refresh" content="8" />
			</noscript>

			<title>Login - Semantic chat</title>

			<link rel="stylesheet" href="css/blueprint/screen.css" type="text/css" media="screen,projection,tv" />
			<link rel="stylesheet" href="css/blueprint/print.css" type="text/css" media="print" />
			<link rel="stylesheet" href="css/screen.css" type="text/css" media="screen,projection,tv" />
			<!--[if lt IE 8]>
			<link rel="stylesheet" href="css/blueprint/ie.css" type="text/css" media="screen,projection,tv" />
			<link rel="stylesheet" href="css/screen-ie.css" type="text/css" media="screen,projection,tv" />
			<![endif]-->

			<script type="text/javascript" src="js/jquery/jquery-1.4.2.js"><!-- --></script>
			<script type='text/javascript' src='dwr/engine.js'><!-- --></script>
			<script type="text/javascript" src="dwr/util.js"><!-- --></script>
			<script type='text/javascript' src='dwr/interface/chatService.js'><!-- --></script>
			<script type="text/javascript" src="js/behavior.js"><!-- --></script>
		</head>

		<body id="page-login">
			<form id="login" action="login.do" method="post">
				<p>
					<input id="login-name" type="text" name="name" maxlength="10" />
					<input type="submit" name="submit" value="Log in" />
				</p>
			</form>
		</body>
	</html>
</jsp:root>