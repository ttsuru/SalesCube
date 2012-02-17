SELECT
	SLIP.HIST_ID,
	SLIP.ACTION_TYPE,
	SLIP.ACTION_FUNC,
	SLIP.REC_DATETM,
	SLIP.REC_USER,
	SLIP.ESTIMATE_SHEET_ID,
	SLIP.ESTIMATE_ANNUAL,
	SLIP.ESTIMATE_MONTHLY,
	SLIP.ESTIMATE_YM,
	SLIP.ESTIMATE_DATE,
	SLIP.DELIVERY_INFO,
	SLIP.VALID_DATE,
	SLIP.USER_ID,
	SLIP.USER_NAME,
	SLIP.REMARKS,
	SLIP.TITLE,
	SLIP.ESTIMATE_CONDITION,
	SLIP.SUBMIT_NAME,
	SLIP.SUBMIT_PRE_CATEGORY,
	SLIP.SUBMIT_PRE,
	SLIP.CUSTOMER_CODE,
	SLIP.CUSTOMER_NAME,
	SLIP.CUSTOMER_REMARKS,
	SLIP.CUSTOMER_COMMENT_DATA,
	SLIP.DELIVERY_NAME,
	SLIP.DELIVERY_OFFICE_NAME,
	SLIP.DELIVERY_DEPT_NAME,
	SLIP.DELIVERY_ZIP_CODE,
	SLIP.DELIVERY_ADDRESS_1,
	SLIP.DELIVERY_ADDRESS_2,
	SLIP.DELIVERY_PC_NAME,
	SLIP.DELIVERY_PC_KANA,
	SLIP.DELIVERY_PC_PRE_CATEGORY,
	SLIP.DELIVERY_PC_PRE,
	SLIP.DELIVERY_TEL,
	SLIP.DELIVERY_FAX,
	SLIP.DELIVERY_EMAIL,
	SLIP.DELIVERY_URL,
	SLIP.CTAX_PRICE_TOTAL,
	SLIP.COST_TOTAL,
	SLIP.RETAIL_PRICE_TOTAL,
	SLIP.ESTIMATE_TOTAL,
	SLIP.TAX_FRACT_CATEGORY,
	SLIP.PRICE_FRACT_CATEGORY,
	SLIP.MEMO,
	SLIP.CRE_FUNC,
	SLIP.CRE_DATETM,
	SLIP.CRE_USER,
	SLIP.UPD_FUNC,
	SLIP.UPD_DATETM,
	SLIP.UPD_USER
FROM
	ESTIMATE_SHEET_TRN_HIST_/*$domainId*/ SLIP
/*BEGIN*/
WHERE
	/*IF recDateFrom != null */
	AND CAST(SLIP.REC_DATETM AS DATE) >= CAST(/*recDateFrom*/'2010/01/01' AS DATE)
	/*END*/
	/*IF recDateTo != null */
	AND CAST(SLIP.REC_DATETM AS DATE) <= CAST(/*recDateTo*/'2010/01/01' AS DATE)
	/*END*/
	/*IF estimateDateFrom != null */
	AND SLIP.ESTIMATE_DATE >= CAST(/*estimateDateFrom*/'2010/01/01' AS DATE)
	/*END*/
	/*IF estimateDateTo != null */
	AND SLIP.ESTIMATE_DATE <= CAST(/*estimateDateTo*/'2010/01/01' AS DATE)
	/*END*/
/*END*/
ORDER BY SLIP.HIST_ID
