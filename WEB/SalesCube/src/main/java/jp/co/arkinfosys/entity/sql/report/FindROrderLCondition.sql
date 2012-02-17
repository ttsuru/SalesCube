SELECT
	DISTINCT
	LINE.HIST_ID,
	LINE.ACTION_TYPE,
	LINE.ACTION_FUNC,
	LINE.REC_DATETM,
	LINE.REC_USER,
	LINE.RO_LINE_ID,
	LINE.STATUS,
	LINE.RO_SLIP_ID,
	LINE.LINE_NO,
	LINE.ESTIMATE_LINE_ID,
	LINE.LAST_SHIP_DATE,
	LINE.PRODUCT_CODE,
	LINE.CUSTOMER_PCODE,
	LINE.PRODUCT_ABSTRACT,
	LINE.PRODUCT_REMARKS,
	LINE.QUANTITY,
	LINE.UNIT_PRICE,
	LINE.UNIT_CATEGORY,
	LINE.UNIT_NAME,
	LINE.PACK_QUANTITY,
	LINE.UNIT_RETAIL_PRICE,
	LINE.RETAIL_PRICE,
	LINE.UNIT_COST,
	LINE.COST,
	LINE.TAX_CATEGORY,
	LINE.CTAX_RATE,
	LINE.CTAX_PRICE,
	LINE.REMARKS,
	LINE.EAD_REMARKS,
	LINE.REST_QUANTITY,
	LINE.RACK_CODE_SRC,
	LINE.CRE_FUNC,
	LINE.CRE_DATETM,
	LINE.CRE_USER,
	LINE.UPD_FUNC,
	LINE.UPD_DATETM,
	LINE.UPD_USER
FROM
	RO_SLIP_TRN_HIST_/*$domainId*/ SLIP
	INNER JOIN RO_LINE_TRN_HIST_/*$domainId*/ LINE ON SLIP.RO_SLIP_ID = LINE.RO_SLIP_ID
/*BEGIN*/
WHERE
	/*IF recDateFrom != null */
	AND CAST(LINE.REC_DATETM AS DATE) >= CAST(/*recDateFrom*/'2010/01/01' AS DATE)
	/*END*/
	/*IF recDateTo != null */
	AND CAST(LINE.REC_DATETM AS DATE) <= CAST(/*recDateTo*/'2010/01/01' AS DATE)
	/*END*/
	/*IF customerCodeFrom != null */
	AND SLIP.CUSTOMER_CODE >= /*customerCodeFrom*/'S'
	/*END*/
	/*IF customerCodeTo != null */
	AND SLIP.CUSTOMER_CODE <= /*customerCodeTo*/'S'
	/*END*/
	/*IF shipDateFrom != null */
	AND SLIP.SHIP_DATE >= CAST(/*shipDateFrom*/'2010/01/01' AS DATE)
	/*END*/
	/*IF shipDateTo != null */
	AND SLIP.SHIP_DATE <= CAST(/*shipDateTo*/'2010/01/01' AS DATE)
	/*END*/
	/*IF productCodeFrom != null */
	AND LINE.PRODUCT_CODE >= /*productCodeFrom*/'S'
	/*END*/
	/*IF productCodeTo != null */
	AND LINE.PRODUCT_CODE <= /*productCodeTo*/'S'
	/*END*/
/*END*/
ORDER BY LINE.HIST_ID
