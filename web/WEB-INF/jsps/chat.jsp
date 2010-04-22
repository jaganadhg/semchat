<?xml version="1.0" encoding="UTF-8"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
		  xmlns:c="http://java.sun.com/jsp/jstl/core"
		  xmlns:chat="urn:jsptagdir:/WEB-INF/tags"
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

			<title>Semantic chat</title>

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

		<body id="page-chat">
			<div id="main">
				<form id="send" action="send.do" method="post">
					<p>
						<input id="send-text" type="text" name="text" maxlength="150" />
						<input type="submit" name="submit" value="Pošli" />
					</p>
				</form>

				<ul id="messages">
					<c:forEach var="message" items="${messages}">
						<li><chat:message message="${message}" showTime="true" /></li>
					</c:forEach>
				</ul>
			</div>
	
			<div id="sidebar">
				<a id="logout" href="logout.do">Log out</a>

				<h2>Current discussion (ID "${currentSegment.id})</h2>
				<h3>Concepts: </h3>
				<c:forEach var="oUri" items="${currentSegment.ontology.classes}"
						   varStatus="status">
					${oUri.resourceName}
					<c:if test="${not status.last}">, </c:if>
				</c:forEach>

				<h2>Similar discussions</h2>
				<ul id="similar-segments">
					<c:forEach var="similarity" items="${similarSegments}">
						<li>
							<h3>Discussion ${similarity.target.id}</h3>

							<h4>Similarity: </h4>
							${similarity.value}

							<h4>Common concepts: </h4>
							<c:forEach var="oUri" items="${similarity.classesJoin}"
									   varStatus="status">
								${oUri.resourceName}
								<c:if test="${not status.last}">, </c:if>
							</c:forEach>
						</li>
					</c:forEach>
				</ul>
			</div>
		</body>
	</html>
</jsp:root>