<?xml version="1.0" encoding="UTF-8"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
		  xmlns:c="http://java.sun.com/jsp/jstl/core"
		  xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
		  xmlns:fn="http://java.sun.com/jsp/jstl/functions"
		  version="2.1">

<h2>Current discussion (ID #${fn:substring(currentSegment.id, 0, 6)})</h2>
<dl>
	<div class="item">
		<dt>Clases: </dt>
		<dd>${currentSegment.ontology.classes}</dd>
	</div>

	<div class="item">
		<dt>Instances: </dt>
		<dd>${currentSegment.ontology.instances}</dd>
	</div>
</dl>

<h2>Similar discussions</h2>
<ul id="similar-segments">
	<c:forEach var="similarity" items="${similarSegments}">
		<c:set var="segmentId" scope="page" value="${similarity.target.id}" />
		<c:set var="segmentIdShort" scope="page" value="${fn:substring(segmentId, 0, 6)}" />
		<li>
			<h3>
				<a href="segment.html?id=${segmentId}">Discussion #${segmentIdShort}</a>
			</h3>

			<dl>
				<div class="item">
					<dt>Similarity: </dt>
					<dd>
						~ <fmt:formatNumber type="percent" value="${similarity.value}" />
					</dd>
				</div>

				<div class="item">
					<dt>Common classes: </dt>
					<dd>${similarity.commonClasses}</dd>
				</div>

				<div class="item">
					<dt>Common instances: </dt>
					<dd>${similarity.commonInstances}</dd>
				</div>
			</dl>
		</li>
	</c:forEach>
</ul>

</jsp:root>