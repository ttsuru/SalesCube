<%@page import="jp.co.arkinfosys.common.CategoryTrns" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="ja">
<head>
	<title><bean:message key='titles.system'/>　セット商品マスタ管理（検索）</title>
	<%@ include file="/WEB-INF/view/common/header.jsp" %>

	<script type="text/javascript">
	<!--
    var paramDataTmp = null;
	
	$(
		function() {
			$("#setProductCode").focus();
		}
	);

	//// Fボタン
	// F1
	function onF1(){
		init_screen();				// 初期化
	}

	// F2
	function onF2(){
		search_productSet();	// 検索
	}

	////

	// 初期化
	function init_screen(){
		// 入力内容を初期化してよろしいですか？
		if(confirm('<bean:message key="confirm.init" />')){
			window.location.doHref('${f:url("/master/searchProductSet")}');
		}
	}

	// 検索
	function search_productSet(){
		if(!confirm('<bean:message key="confirm.search" />')){
			return;
		}
		return execSearch(createData(), true);
	}

	//ページ繰り、ソートによる検索処理
	function goPage(page){
		var data = createData();
		data["pageNo"] = page;
		return execSearch(data);
	}

	/**
	 * クリック前のソート列
	 */
	function sort(itemId) {
		if($("#sortColumn").attr("value") == itemId) {
			// 同じ項目の場合は順序を反転する
			if($("#sortOrderAsc").attr("value") == "true") {
				$("#sortOrderAsc").attr("value", false);
			}
			else {
				$("#sortOrderAsc").attr("value", true);
			}
		}
		else {
			$("#sortOrderAsc").attr("value", true);
		}
		// ソート列を設定する
		$("#sortColumn").attr("value", itemId);

		// 1回以上検索しており、前回の結果が1件以上ある場合のみ再検索
		if(paramDataTmp != null && $("#searchResultCount").val() != "0") {
			// 前回の検索条件からソート条件のみを変更
			var paramData = paramDataTmp;
			paramData["pageNo"] = 1;
			paramData["sortColumn"] = $("#sortColumn").val();
			paramData["sortOrderAsc"] = $("#sortOrderAsc").val();
			// 検索
			return execSearch(paramData);
		}
	}

	function execSearch(paramData, autoEdit) {
		if(!paramData["pageNo"]) {
			// ページの設定がなければ1ページ
			paramData["pageNo"] = 1;
		}

		// 検索実行
		asyncRequest(
			"${f:url("/ajax/master/searchProductSetAjax/search")}",
			paramData,
			function(data) {
				// 検索結果件数が1件であれば編集画面に遷移する
				var jData = $(data);
				if(autoEdit && jData.is("#singleProductSetCode")) {
					var productSetCode = jData.filter("#singleProductSetCode");
					window.location.doHref("${f:url("/master/editProductSet/edit/")}" + productSetCode.val());
					return;
				}

				// 検索結果テーブルを更新する
				$("#ListContainer").empty();
				$("#ListContainer").append(data);

				// 1件以上ヒットした場合
				if($("#searchResultCount").val() != "0") {
					// 検索条件を保持
					paramDataTmp = paramData;
				}
			}
		);
	}

	/**
	 * リクエストパラメータ作成
	 */
	function createData(prev){
		// リクエストデータ作成
		var data = new Object();
		var prev = "";
		if(prev) {
			prev = "prev_";
		}

		// セット商品コード
		var id = "#" + prev + "setProductCode";
		if($(id).val()) {
			data["setProductCode"] = $(id).val();
		}
		// セット商品名
		id = "#" + prev + "setProductName";
		if($(id).val()) {
			data["setProductName"] = $(id).val();
		}

		// 商品コード
		var id = "#" + prev + "productCode";
		if($(id).val()) {
			data["productCode"] = $(id).val();
		}
		// 商品名
		id = "#" + prev + "productName";
		if($(id).val()) {
			data["productName"] = $(id).val();
		}

		// 表示件数
		id = "#" + prev + "rowCount";
		if(prev) {
			data["rowCount"] = $(id).attr("value");
		}
		else {
			var rowCount = $(id).get(0);
			data["rowCount"] = rowCount.options[ rowCount.selectedIndex ].value;
		}

		// ソート列
		id = "#" + prev + "sortColumn";
		data["sortColumn"] = $(id).val();

		// ソート昇順フラグ
		id = "#" + prev + "sortOrderAsc";
		data["sortOrderAsc"] = $(id).val();

		return data;
	}

	/**
	 * 編集
	 */
	function editProductSet(setProductCode){
		$("#editForm").find("#setProductCode").val(setProductCode);
		$("#editForm").submit();
	}

	/**
	 * 商品検索ダイアログを開く
	 */
	function productSearch(jqObject, isSet) {
		var dialogId = jqObject.attr("id") + "Dialog";
		openSearchProductDialog(
			dialogId,
			function(id, map) {
				if(jqObject.attr("id").search(/Code$/) > 0) {
					jqObject.val( map[ "productCode" ] );
				}
				else if(jqObject.attr("id").search(/Name$/) > 0) {
					jqObject.val( map[ "productName" ] );
				}
			}
		);

		// ダイアログのフィールドに値をセットしてフォーカス
		if(jqObject.attr("id").search(/Code$/) > 0) {
			$("#" + dialogId + "_productCode").val( jqObject.val() );
			$("#" + dialogId + "_productCode").focus();
		}
		else if(jqObject.attr("id").search(/Name$/) > 0) {
			$("#" + dialogId + "_productName").val( jqObject.val() );
			$("#" + dialogId + "_productName").focus();
		}

		// 適切なセット分類を選択
		var target = "<%=CategoryTrns.PRODUCT_SET_TYPE_SINGLE%>"
		if(isSet) {
			target = "<%=CategoryTrns.PRODUCT_SET_TYPE_SET%>";
		}
		var i = 0;
		var setTypeCategory = $("#" + dialogId + "_setTypeCategory");
		setTypeCategory.children("option").each(
			function() {
				if(this.value == target) {
					setTypeCategory.get(0).selectedIndex = i;
				}
				i++;
			}
		);
	}

	-->
	</script>

</head>
<body onhelp="return false;">
	
	<%@ include file="/WEB-INF/view/common/titlebar.jsp" %>

	
	<jsp:include page="/WEB-INF/view/common/menubar.jsp">
		<jsp:param name="PARENT_MENU_ID" value="0013"/>
		<jsp:param name="MENU_ID" value="1301"/>
	</jsp:include>

	
	<div id="main_function">
		<span class="title">セット商品</span>

		
		<s:form onsubmit="return false;">

			
			<div class="function_buttons">
				<button type="button" tabindex="2000" onclick="init_screen();">F1<br>初期化</button><button
					type="button" tabindex="2001" onclick="search_productSet();">F2<br>検索</button><button
					type="button" disabled="disabled">F3<br>&nbsp;</button><button
					type="button" disabled="disabled">F4<br>&nbsp;</button><button
					type="button" disabled="disabled">F5<br>&nbsp;</button><button
					type="button" disabled="disabled">F6<br>&nbsp;</button><button
					type="button" disabled="disabled">F7<br>&nbsp;</button><button
					type="button" disabled="disabled">F8<br>&nbsp;</button><button
					type="button" disabled="disabled">F9<br>&nbsp;</button><button
					type="button" disabled="disabled">F10<br>&nbsp;</button><button
					type="button" disabled="disabled">F11<br>&nbsp;</button><button
					type="button" disabled="disabled">F12<br>&nbsp;</button>
			</div>

			
			<div class="function_forms">
				
				<div style="color:red; padding-left: 20px">
					<span id="ajax_errors">
						<html:errors/>
						<html:messages id="msg" message="true">
							<bean:write name="msg" ignore="true"/>
						</html:messages>
					</span>
				</div>

				<span>セット商品情報</span><br>

				
				<table class="forms" summary="セット商品">
					<colgroup>
						<col span="1" style="width: 15%">
						<col span="1" style="width: 25%">
						<col span="1" style="width: 15%">
						<col span="1" style="width: 45%">
					</colgroup>
					<tr>
						<th>セット商品コード</th>
						<td>
							<html:text styleId="setProductCode" property="setProductCode"
								style="width: 170px; ime-mode: disabled;" tabindex="100"
								onfocus="this.curVal=this.value;" onblur="if(this.curVal!=this.value){ this.value=this.value.toUpperCase(); }"/>
							<html:image src="${f:url('/images/icon_04_02.gif')}" style="vertical-align: middle; cursor: pointer;"
								onclick="productSearch($('#setProductCode'),true);" tabindex="101"/>
						</td>
						<th>セット商品名</th>
						<td><html:text styleId="setProductName" property="setProductName"
								style="width: 400px" tabindex="102" />
							<html:image src="${f:url('/images/icon_04_02.gif')}" style="vertical-align: middle; cursor: pointer;"
								onclick="productSearch($('#setProductName'),true);" tabindex="103"/>
						</td>
					</tr>
				</table>

				
				<table class="forms" summary="セット内容">
					<colgroup>
						<col span="1" style="width: 7%">
						<col span="1" style="width: 8%">
						<col span="1" style="width: 85%">
					</colgroup>
					<tr>
						<th rowspan="2">セット内容</th>
						<th>商品コード</th>
						<td><html:text styleId="productCode" property="productCode"
								style="width: 170px; ime-mode: disabled;" tabindex="200"
								onfocus="this.curVal=this.value;" onblur="if(this.curVal!=this.value){ this.value=this.value.toUpperCase(); }"/>

						<html:image src="${f:url('/images/icon_04_02.gif')}" style="vertical-align: middle; cursor: pointer;"
							onclick="productSearch($('#productCode'));" tabindex="201"/>

						</td>
					</tr>
					<tr>
						<th>商品名</th>
						<td>
							<html:text styleId="productName" property="productName"
								style="width: 400px" tabindex="202" />

							<html:image src="${f:url('/images/icon_04_02.gif')}" style="vertical-align: middle; cursor: pointer;"
								onclick="productSearch($('#productName'));" tabindex="203"/>

						</td>
					</tr>
				</table>

				<div style="width: 910px; text-align: right">
					<button type="button" tabindex="250" style="width:80px" onclick="init_screen();">初期化</button>
					<button type="button" tabindex="251" style="width:80px" onclick="search_productSet();">検索</button>
				</div>

				<div id="ListContainer">
					<div style="width: 910px; height: 25px;">
							<div style="position:absolute; left: 0px;">検索結果件数： 0件</div>
		                    <jsp:include page="/WEB-INF/view/common/rowcount.jsp"/>
					</div>
					<table id="List" summary="セット商品検索結果"  class="forms" style="width: 910px">
						<colgroup>
							<col span="1" style="width: 20%">
							<col span="1" style="width: 70%">
							<col span="1" style="width: 10%">
						</colgroup>
						<tr>
							<th style="cursor: pointer">セット商品コード</th>
							<th style="cursor: pointer">セット商品名</th>
							<th style="cursor: pointer">&nbsp;</th>
						</tr>
					</table>
				</div>
			</div>

			<html:hidden styleId="sortColumn" property="sortColumn" value="productCode"/>
			<html:hidden styleId="sortOrderAsc" property="sortOrderAsc" value="true"/>

		</s:form>

		<s:form styleId="editForm" action="/master/editProductSet/edit" >
			<input type="hidden" id="setProductCode" name="setProductCode">
		</s:form>
		
	</div>
</body>

</html>
