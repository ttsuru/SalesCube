<table style="width: 710px;">
	<tr>
		<td style="text-align: left;">検索結果件数: ${searchResultCount}件</td>
		<td style="text-align: right; white-space: normal;">
			<span style="color: red">
			 	<html:messages id="resultThreshold" message="true">
			 		<bean:write name="resultThreshold" filter="false"/>
			 	</html:messages>
			</span>
		</td>
	</tr>
</table>

<div
	style="padding: 0px; border: none; width: 710px; height: 240px; overflow: hidden;">
<table id="${dialogId}List" summary="顧客検索結果"
	style="width: 700px;">
	<colgroup>
		<col span="1" style="width: 5%">
		<col span="1" style="width: 10%">
		<col span="1" style="width: 20%">
		<col span="1" style="width: 10%">
		<col span="1" style="width: 10%">
		<col span="1" style="width: 8%">
		<col span="1" style="width: 13%">
		<col span="1" style="width: 12%">
		<col span="1" style="width: 12%">
	</colgroup>
	<tr>
		<th>&nbsp;</th>
		<th>顧客コード</th>
		<th>顧客名</th>
		<th>TEL</th>
		<th>担当者</th>
		<th>取引区分</th>
		<th>支払条件</th>
		<th>事業所名</th>
		<th>部署名</th>
	</tr>
	<c:forEach var="bean" items="${searchResultList}" varStatus="status">
		<tr>
			<td style="text-align: center;"><input type="radio"
				name="${dialogId}_selectedCustomer" value="${f:h(bean.customerCode)}"
				tabindex="6100"
				onclick="$('#${dialogId}_selectButton').attr('disabled', false);">
			</td>
			<td>${f:h(bean.customerCode)}</td>
			<td>${f:h(bean.customerName)}</td>
			<td>${f:h(bean.customerTel)}</td>
			<td>${f:h(bean.customerPcName)}</td>
			<td>${f:h(bean.categoryCodeName3)}</td>
			<td>${f:h(bean.categoryCodeName)}</td>
			<td>${f:h(bean.customerOfficeName)}</td>
			<td>${f:h(bean.customerDeptName)}</td>
		</tr>
	</c:forEach>
</table>
</div>