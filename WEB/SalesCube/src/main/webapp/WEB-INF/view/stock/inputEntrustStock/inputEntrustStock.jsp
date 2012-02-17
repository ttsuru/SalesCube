<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="jp.co.arkinfosys.common.CategoryTrns"%>

<%@page import="jp.co.arkinfosys.common.Constants"%><html lang="ja">
<head>
	<title><bean:message key='titles.system'/> <bean:message key='titles.inputEntrustStock'/></title>
	<%@ include file="/WEB-INF/view/common/header.jsp" %>
	<script type="text/javascript">
	<!--
		var MAX_LINE_ROW_COUNT = ${f:h(maxLineRowCount)};
		var MAIN_FORM_NAME = "stock_inputEntrustStockActionForm";
		var maxIndex = 0;
		var trCloneBase = null;
		var baseIndex = 0;
		var lineCount;		// 現在表示している明細行の行数
		var initFormElementSize = 0;
		var initValuesDigest = "";

		// ページ読込時の動作
		$(document).ready(function(){

			var initForms = $(document.purchase_inputPurchaseActionForm).find("input, select, textarea");
			initFormElementSize = initForms.size();

			initForms.each(
				function() {
					initValuesDigest += this.value;
				}
			);

			// 明細行のIndex管理
			var maxLineNo = $("#tbodyLine").children().length-1;
			lineCount = maxLineNo + 1;
			var maxLineId
			if(maxLineNo >= 0) {
				maxLineId = $("#tbodyLine").children().eq(maxLineNo).attr("id");
				maxIndex = maxLineId.replace("trLine", "");

				// 明細が複数行存在する時は、委託入出庫番号と発注番号、委託入出庫区分をDisableにする
				$("#entrustEadSlipId").attr("readOnly", "true");
				$("#entrustEadSlipId").addClass("c_disable");
				$("#poSlipId").attr("readOnly", "true");
				$("#poSlipId").addClass("c_disable");

				// 明細行のクローンを生成
				trCloneBase = $("#tbodyLine").children(":first").clone(true);
				baseIndex = trCloneBase.attr("id").replace("trLine", "");
				// 明細行の項目にイベントをバインド
				for(var i=0; i<=maxIndex; i++) {
					$("#stockButton" + i).bind("click", {index: i}, openStockDialog);
					applyNumeralStylesToObj(${mineDto.productFractCategory}, ${mineDto.numDecAlignment}, $("#entrustEadLineTrnDtoList\\[" + i + "\\]\\.quantity"));
				}
			}

			// 初期フォーカス設定
//			$("#entrustEadDate").focus();
			if( $("#entrustEadSlipId").val() != "" ){
				$("#entrustEadSlipId").attr("readOnly", "true");
				$("#entrustEadSlipId").addClass("c_disable");
				$("#entrustEadDate").focus();

			}else{
				$("#entrustEadSlipId").focus();
			}
		});

		function findSlip(){
			if( $("#entrustEadSlipId").val() == "" || lineCount > 0 ){
			}else{
				showNowSearchingDiv();
				$("form[name='" + MAIN_FORM_NAME + "']").attr("action", '${f:url("/stock/inputEntrustStock/load")}' );
				$("form[name='" + MAIN_FORM_NAME + "']").submit();
			}
		}

		function copySlipFromPorder() {
			if( $("#poSlipId").val() != "" && lineCount <= 0 && $("#entrustEadCategory").val() != "" ){

				// 複写対象の委託入出庫区分を確定する
				$("#copySlipFixedEntrustEadCategory").val( $("#entrustEadCategory").val() );

				showNowSearchingDiv();
				_before_submit($(".numeral_commas"));
				$("#copySlipId").val($("#poSlipId").val());
				$("form[name='" + MAIN_FORM_NAME + "']").attr("action", '${f:url("/stock/inputEntrustStock/copySlipFromPorderLoad")}' );
				$("form[name='" + MAIN_FORM_NAME + "']").submit();
			}
		}

		// 初期化
		function onF1(){
			// 入力内容を初期化してよろしいですか？
			if(confirm('<bean:message key="confirm.init" />')){
				showNowSearchingDiv();

				// F1ボタンを押下すると、常に新規とする。
				<bean:define id="concatUrl" value="${'/stock/inputEntrustStock'}" />
				location.doHref('${f:url(concatUrl)}');

			}
		}

		// 削除
		function onF2(){
			// この行を削除してよろしいですか？
			if(confirm('<bean:message key="confirm.delete" />')){
				showNowSearchingDiv();
				_before_submit($(".numeral_commas"));
				$("form[name='" + MAIN_FORM_NAME + "']").attr("action", '${f:url("/stock/inputEntrustStock/delete")}');
				$("form[name='" + MAIN_FORM_NAME + "']").submit();
			}
		}

		// 登録
		function onF3(){
<c:if test="${!isExistSlipRead}">
			if(confirm('<bean:message key="confirm.insert" />')){
</c:if>
<c:if test="${isExistSlipRead}">
			if(confirm('<bean:message key="confirm.update" />')){
</c:if>
				showNowSearchingDiv();
				_before_submit($(".numeral_commas"));
				$("form[name='" + MAIN_FORM_NAME + "']").attr("action", '${f:url("/stock/inputEntrustStock/upsert")}');
				$("form[name='" + MAIN_FORM_NAME + "']").submit();
			}
		}

		function onF5() {
			if(confirm('<bean:message key="confirm.pdf" />')){
				var form = $(window.document.forms["PDFOutputForm"]);
				var hidden = $(document.createElement("input"));
				hidden.attr("type", "hidden");
				hidden.attr("name", "entrustEadSlipId");
				hidden.val("${f:h(entrustEadSlipId)}");
				form.append(hidden);
				form.submit();
			}
		}

		// 伝票複写ボタン
		function onF6(){
			openCopySlipDialog('1007', 'copySlipFrom1007',
					function (dialogId, slipName, copySlipId) {
				if(isChangedForm() && !confirm("<bean:message key='confirm.copyslip'/>")){
					// フォームが変更されている場合
					return;
				}

				if( slipName == "ENTRUST_PORDER" ){
					// 発注伝票
					$("#copySlipId").val(copySlipId) ;
					document.stock_inputEntrustStockActionForm.action = '${f:url("copy")}';
					document.stock_inputEntrustStockActionForm.submit();
				}
			}
			);

			// 伝票複写ダイアログのhidden項目に、委託入庫で呼び出されたか出庫で呼び出されたかの情報をセットする
			if( $("#entrustEadCategory").val() == "<%=CategoryTrns.ENTRUST_EAD_CATEGORY_ENTER%>" ) {
				$("#copySlipFrom1007_entrustPorderCondition\\.targetPoLineStatus").val("<%=Constants.STATUS_PORDER_LINE.ORDERED%>");
				$("#copySlipEntrustEadCategory").val("<%=CategoryTrns.ENTRUST_EAD_CATEGORY_ENTER%>");
			} else if($("#entrustEadCategory").val() == "<%=CategoryTrns.ENTRUST_EAD_CATEGORY_DISPATCH%>") {
				$("#copySlipFrom1007_entrustPorderCondition\\.targetPoLineStatus").val("<%=Constants.STATUS_PORDER_LINE.ENTRUST_STOCK_MAKED%>");
				$("#copySlipEntrustEadCategory").val("<%=CategoryTrns.ENTRUST_EAD_CATEGORY_DISPATCH%>");
			} else {
				$("#copySlipFrom1007_entrustPorderCondition\\.targetPoLineStatus").val("");
				$("#copySlipEntrustEadCategory").val("");
			}
		}

		// 伝票複写ダイアログの委託入出庫区分が変更されたとき
		function changeEntrustEadCategoryOnCopyDialog() {
			if($("#copySlipEntrustEadCategory").val() == "<%=CategoryTrns.ENTRUST_EAD_CATEGORY_ENTER%>") {
				$("#copySlipFrom1007_entrustPorderCondition\\.targetPoLineStatus").val("<%=Constants.STATUS_PORDER_LINE.ORDERED%>");
			} else if($("#copySlipEntrustEadCategory").val() == "<%=CategoryTrns.ENTRUST_EAD_CATEGORY_DISPATCH%>") {
				$("#copySlipFrom1007_entrustPorderCondition\\.targetPoLineStatus").val("<%=Constants.STATUS_PORDER_LINE.ENTRUST_STOCK_MAKED%>");
			} else {
				$("#copySlipFrom1007_entrustPorderCondition\\.targetPoLineStatus").val("");
			}
		}

		/**
		 * フォームの内容が変更されているか否かを確認する
		 * 変更されている:true
		 */
		function isChangedForm() {
			var currentForms = $(document.stock_inputEntrustStockActionForm).find("input, select, textarea");
			if(initFormElementSize != currentForms.size()) {
				return true;
			}

			var currentValuesDigest = "";
			currentForms.each(
				function() {
					currentValuesDigest += this.value;
				}
			);
			if(initValuesDigest != currentValuesDigest) {
				return true;
			}

			return false;
		}

		// 明細行の空行判定
		function isEmptyLine(lineNo){
			var retVal = true;

			// 商品コード
			if($("#entrustEadLineTrnDtoList\\["+lineNo+"\\]\\.productCode").val() != "") {
				retVal = false;
			}
			// 数量
			if($("#entrustEadLineTrnDtoList\\["+lineNo+"\\]\\.quantity").val() != "") {
				retVal = false;
			}
			// 備考
			if($("#entrustEadLineTrnDtoList\\["+lineNo+"\\]\\.remarks").val() != "") {
				retVal = false;
			}

			return retVal;
		}

		// 指定行から前行のIndexを返す
		// （見つからない場合は-1を返す）
		function getPrevIndex(index){
			var retVal = -1;
			var i, lineNo, trId;

			// 指定行の行番号を取得
			lineNo = parseInt($("#entrustEadLineTrnDtoList\\["+index+"\\]\\.lineNo").val());
			// 前行を探す（削除行は非表示になっている為、直前の削除されていない行を探す）
			if(lineNo>1) {
				trId = $("#tbodyLine").get(0).children[lineNo-2].id;
				retVal = trId.replace("trLine", "");
			}

			return retVal;
		}

		// 商品コード変更
		function changeProductCode(event) {
			searchProductCode(event.data.index);
		}

		// 商品検索
		function searchProductCode(index) {
			var map = new Object();
			var lineNo = parseInt($("#entrustEadLineTrnDtoList\\["+index+"\\]\\.lineNo").val());
			var label = '<bean:message key="labels.productCode" />';
			var code = $("#entrustEadLineTrnDtoList\\["+index+"\\]\\.productCode").val();

			if($("#entrustEadLineTrnDtoList\\["+index+"\\]\\.productCode").val() == "") {
				map["productCode"] = "";
				map["productName"] = "";
				setProductInfo(index, map);
				return;
			}
			var data = new Object();
			data["productCode"] = $("#entrustEadLineTrnDtoList\\["+index+"\\]\\.productCode").val();
			asyncRequest(
				contextRoot + "/ajax/commonProduct/getProductInfos",
				data,
				function(data) {
					if(data==""){
						alert('<bean:message key="errors.notExist" arg0="'+label+'" />');
						//map["productCode"] = "";
						map["productName"] = "";
						setProductInfo(index, map);
					} else {
						var value = eval("(" + data + ")");
						// 商品コード
						$("#entrustEadLineTrnDtoList\\["+index+"\\]\\.productCode").val(value.productCode);
						// 商品名（表示用）
						$("#productAbstract"+index).text(value.productName);
						// 商品名
						$("#entrustEadLineTrnDtoList\\["+index+"\\]\\.productAbstract").val(value.productName);
					}
				}
			);
		}

		// 商品検索
		function openStockDialog(event) {
	        var id = event.data.lineNo;
	        var productCode = $("#entrustEadLineTrnDtoList\\["+event.data.index+"\\]\\.productCode").val();
	        openStockInfoDialog('stockInfo', productCode);
		}

		// 商品検索後の設定処理
		function setProductInfoFromDialog(index, map) {
			setProductInfo(index, map);
			searchProductCode(index);
		}

		// 商品検索後の設定処理
		function setProductInfo(index, map) {
			// 商品コード
			$("#entrustEadLineTrnDtoList\\["+index+"\\]\\.productCode").val(map["productCode"]);
			// 商品名（表示用）
			$("#productAbstract"+index).text(map["productName"]);
			// 商品名
			$("#entrustEadLineTrnDtoList\\["+index+"\\]\\.productAbstract").val(map["productName"]);
		}

		// 棚番コード変更
		function changeRackCode(event) {
			searchRackCode(event.data.index);
		}

		// 全て選択・全て解除
		 function checkAll(flg){
		 	$("#input_table").find("input[type='checkbox']").attr('checked', flg);
		 }

	-->
	</script>
</head>
<body>
	
	<%@ include file="/WEB-INF/view/common/titlebar.jsp" %>

	
	<jsp:include page="/WEB-INF/view/common/menubar.jsp">
		<jsp:param name="PARENT_MENU_ID" value="0010"/>
		<jsp:param name="MENU_ID" value="1007"/>
	</jsp:include>

	
	<div id="main_function">

		<span class="title"><bean:message key='titles.inputEntrustStock'/></span>

		<div class="function_buttons">
			<button type="button" id="btnF1" tabindex="2000" onclick="onF1();">F1<br><bean:message key='words.action.initialize'/><%// 初期化 %>
			</button><button type="button" id="btnF2" tabindex="2001" onclick="onF2();" ${(menuUpdate&&isExistSlipRead)?"":"disabled"}>F2<br><bean:message key='words.action.delete'/><%// 削除 %>
<c:if test="${!isExistSlipRead}">
			</button><button type="button" id="btnF3" tabindex="2002" onclick="onF3();" ${menuUpdate?"":"disabled"}>F3<br><bean:message key='words.action.register'/><%// 登録 %>
</c:if>
<c:if test="${isExistSlipRead}">
			</button><button type="button" id="btnF3" tabindex="2002" onclick="onF3();" ${menuUpdate?"":"disabled"}>F3<br><bean:message key='words.action.renew'/><%// 更新 %>
</c:if>
			</button><button type="button" id="btnF4" tabindex="2001" disabled>F4<br>&nbsp;
			</button><button type="button" id="btnF5" tabindex="2004" ${(menuUpdate&&isExistSlipRead)?"":"disabled"} onclick="onF5();">F5<br><bean:message key='words.name.pdf'/>
			</button><button type="button" id="btnF6" tabindex="2005" onclick="onF6();" ${!isExistSlipRead?"":"disabled"}>F6<br>伝票呼出
			</button><button type="button" id="btnF7" tabindex="2006" disabled>F7<br>&nbsp;
			</button><button type="button" id="btnF8" tabindex="2007" disabled>F8<br>&nbsp;
			</button><button type="button" id="btnF9" tabindex="2008" disabled>F9<br>&nbsp;
			</button><button type="button" id="btnF10" tabindex="2009" disabled>F10<br>&nbsp;
			</button><button type="button" id="btnF11" tabindex="2010" disabled>F11<br>&nbsp;
			</button><button type="button" id="btnF12" tabindex="2011" disabled>F12<br>&nbsp;
			</button>
		</div>

		<s:form onsubmit="return false;">

			<div class="function_forms">

				<div style="padding-left: 20px">
					<html:errors />
				</div>
				<div id="messages" style="color: blue;padding-left: 20px">
					<html:messages id="msg" message="true">
						<bean:write name="msg" ignore="true"/><br>
					</html:messages>
				</div>

				<bean:message key='labels.entrustStockSlipInfos'/><br>
				<table id="order_info" class="forms" summary="委託入出庫伝票情報">
					<colgroup>
						<col span="1" style="width: 10%">
						<col span="1" style="width: 15%">
						<col span="1" style="width: 10%">
						<col span="1" style="width: 15%">
						<col span="1" style="width: 10%">
						<col span="1" style="width: 15%">
						<col span="1" style="width: 10%">
						<col span="1" style="width: 15%">
					</colgroup>
					<tr>
						<th><bean:message key='labels.entrustEadSlipId'/></th><%// 委託入出庫番号 %>
						<td><html:text property="entrustEadSlipId" styleId="entrustEadSlipId"  style="width: 140px; ime-mode: disabled;" styleClass="" tabindex="100" readonly="false"  maxlength="10"  onblur="findSlip();"/></td>
						<th><bean:message key='labels.poSlipId'/></th><%// 発注番号 %>
						<td><html:text property="poSlipId" styleId="poSlipId"  style="width: 140px; ime-mode: disabled;" styleClass="" tabindex="100" readonly="false"  maxlength="10"  onblur="copySlipFromPorder();"/></td>
						<th><bean:message key='labels.entrustEadDate'/><bean:message key='labels.must'/></th><%// 入出庫日 %>
						<td><html:text property="entrustEadDate" styleId="entrustEadDate" style="width: 100px; ime-mode: disabled;" styleClass="date_input" tabindex="101" maxlength="10" /></td>
						<th><bean:message key='labels.userName' /></th><%// 入力担当者 %>
						<td>
							<html:text property="userName"  styleClass="c_disable" readonly="true" />
						</td>
					</tr>
					<tr>
						<th><bean:message key='labels.entrustEadCategory'/></th><%// 入出庫区分 %>
						<td>
							<html:select property="entrustEadCategory" styleId="entrustEadCategory" tabindex="103" onblur="copySlipFromPorder();" >
								<html:options collection="categoryList" property="value" labelProperty="label"/>
							</html:select>
						</td>
						<th><bean:message key='labels.remarks'/></th><%// 備考 %>
						<td colspan="5"><html:text property="remarks" styleClass="c_referable" style="width: 570px;" tabindex="104" maxlength="120" /></td>
					</tr>
				</table>
				<html:hidden property="copySlipId" styleId="copySlipId" />
				<html:hidden property="userId"/>
				<html:hidden property="stockPdate" />
				<html:hidden property="updDatetm" />
				<html:hidden property="isExistSlipRead" styleId="isExistSlipRead"  />
				<html:hidden property="dispatchOrderPrintCount" styleId="dispatchOrderPrintCount"  />
				<html:hidden property="copySlipFixedEntrustEadCategory" styleId="copySlipFixedEntrustEadCategory"  />

				<span>仕入先情報</span><br>
				<table id="order_info" class="forms" summary="仕入先情報">
					<colgroup>
						<col span="1" style="width: 10%">
						<col span="1" style="width: 15%">

						<col span="1" style="width: 10%">
						<col span="1" style="width: 65%">
					</colgroup>
					<tr>
						<th>仕入先コード</th>
						<td>
							<html:text property="supplierCode" styleId="supplierCode" styleClass="c_disable" style="width: 130px"  tabindex="200" readonly="true" />
						</td>
						<th>仕入先名</th>
						<td><html:text property="supplierName" styleId="supplierName" style="width: 235px;" styleClass="c_disable" tabindex="202" readonly="true" /></td>
					</tr>
				</table>

				<br>
				<input type="button" id="allCheck" name="allCheck" tabindex="300" value="全て選択" onclick="checkAll(true);" >
				<input type="button" id="allUnCheck" name="allUnCheck" tabindex="301" value="全て解除" onclick="checkAll(false);" >

				
				<table id="input_table" summary="入出庫明細リスト" class="forms" style="margin-top: 0px;">
					<colgroup>
						<col span="1" style="width: 40px;">
						<col span="1" style="width:125px;">
						<col span="1" style="width:235px;">
						<col span="1" style="width: 60px;">
						<col span="1" style="">
					</colgroup>
					<thead>
						<tr>
							<th rowspan="2"><bean:message key='labels.select'/></th><%// 選択 %>
							<th rowspan="2"><bean:message key='labels.productCode'/></th><%// 商品コード %>
							<th rowspan="2"><bean:message key='labels.productName'/></th><%// 商品名 %>
							<th rowspan="2"><bean:message key='labels.quantity'/></th><%// 数量 %>
							<th><bean:message key='labels.productRemarks'/></th><%// 商品備考 %>
						</tr>
						<tr>
							<th colspan="5"><bean:message key='labels.remarks'/></th><%// 備考 %>
						</tr>
					</thead>
					<tbody id="tbodyLine">
						<c:forEach var="entrustEadLineTrnDtoList" items="${entrustEadLineTrnDtoList}" varStatus="status">
							<c:if test='${entrustEadLineTrnDtoList.lineNo != null}'>
								<tr id="trLine${status.index}">
									<td id="tdNo${status.index}" style="text-align: center">
	<c:if test="${!isExistSlipRead}">
										<html:checkbox name="entrustEadLineTrnDtoList" property="checkEadLine"  indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].checkEadLine" />
	</c:if>
									</td>

									<td style="display: none;">
										<html:hidden name="entrustEadLineTrnDtoList" property="lineNo" indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].lineNo" />
										<html:hidden name="entrustEadLineTrnDtoList" property="entrustEadLineId" indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].entrustEadLineId" />
										<html:hidden name="entrustEadLineTrnDtoList" property="relEntrustEadLineId" indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].relEntrustEadLineId" />
										<html:hidden name="entrustEadLineTrnDtoList" property="productCode" indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].productCode" />
										<html:hidden name="entrustEadLineTrnDtoList" property="productAbstract" indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].productAbstract" />
										<html:hidden name="entrustEadLineTrnDtoList" property="poLineId" indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].poLineId" />
									</td>
									<td style="text-align: ${columnInfoList[statusCol.index].textAlign}">
										<span id="productAbstract${status.index}" style="display: block; width:250px; height:3em; white-space: normal; overflow: auto; padding: 1px;">
											<c:out value="${entrustEadLineTrnDtoList.productCode}" />
										</span>
									</td>
									<td style="text-align: ${columnInfoList[statusCol.index].textAlign}">
										<span id="productAbstract${status.index}" style="display: block; width:250px; height:3em; white-space: normal; overflow: auto; padding: 1px;">
											<c:out value="${entrustEadLineTrnDtoList.productAbstract}" />
										</span>
									</td>
									<td style="text-align: ${columnInfoList[statusCol.index].textAlign}">
										<html:text name="entrustEadLineTrnDtoList" property="quantity" indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].quantity" styleClass="c_disable numeral_commas" style="width: 55px; ime-mode: disabled;" tabindex="${status.index*lineElementCount+1002}" maxlength="6"  readonly="true" /><br>
										<button id="stockButton${status.index}">在庫</button>
									</td>
									<td style="text-align: ${columnInfoList[statusCol.index].textAlign}">
										<html:textarea name="entrustEadLineTrnDtoList" property="productRemarks" indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].productRemarks" style="width: 300px; height: 3em;" tabindex="${status.index*lineElementCount+1003}" readonly="true"  styleClass="c_disable" /> <br>
										<html:textarea name="entrustEadLineTrnDtoList" property="remarks" indexed="true" styleId="entrustEadLineTrnDtoList[${status.index}].remarks" style="width: 300px; height: 3em;" tabindex="${status.index*lineElementCount+1004}" />

										<c:out value="${entrustEadLineTrnDtoList.remarks}" />
									</td>
								</tr>
							</c:if>
						</c:forEach>

					</tbody>
				</table>
			</div>
		</s:form>
		<div style="text-align: right; width: 910px">
			<c:if test="${!isExistSlipRead}">
				<button type="button" id="btnF3btm" tabindex="1999" onclick="onF3();" ${menuUpdate?"":"disabled"}><bean:message key='words.action.register'/><%// 登録 %></button>
			</c:if>
			<c:if test="${isExistSlipRead}">
				<button type="button" id="btnF3btm" tabindex="1999" onclick="onF3();" ${menuUpdate?"":"disabled"}><bean:message key='words.action.renew'/><%// 更新 %></button>
			</c:if>
		</div>
	</div>


	<form name="PDFOutputForm" action="${f:url('/stock/outputEntrustStockOrder/pdf')}" style="display: none;" method="POST">
	</form>
</body>

</html>
