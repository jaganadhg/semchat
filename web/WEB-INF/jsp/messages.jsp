<?xml version="1.0" encoding="UTF-8"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
		  xmlns:c="http://java.sun.com/jsp/jstl/core"
		  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
		  version="2.1">

<c:forEach var="message" items="${requestScope.messages}">
	<li>
		<span class="meta">
			<c:out value="${message.user.name} " />
			<fmt:formatDate type="time" value="${message.time}" pattern="H.mm" />:
		</span>
		${message.text}
	</li>
</c:forEach>

</jsp:root>
