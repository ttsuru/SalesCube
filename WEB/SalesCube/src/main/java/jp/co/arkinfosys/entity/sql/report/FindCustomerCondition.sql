SELECT
	SLIP.HIST_ID,
	SLIP.ACTION_TYPE,
	SLIP.ACTION_FUNC,
	SLIP.REC_DATETM,
	SLIP.REC_USER,
	SLIP.CUSTOMER_CODE,
	SLIP.CUSTOMER_NAME,
	SLIP.CUSTOMER_KANA,
	SLIP.CUSTOMER_OFFICE_NAME,
	SLIP.CUSTOMER_OFFICE_KANA,
	SLIP.CUSTOMER_ABBR,
	SLIP.CUSTOMER_DEPT_NAME,
	SLIP.CUSTOMER_ZIP_CODE,
	SLIP.CUSTOMER_ADDRESS_1,
	SLIP.CUSTOMER_ADDRESS_2,
	SLIP.CUSTOMER_PC_POST,
	SLIP.CUSTOMER_PC_NAME,
	SLIP.CUSTOMER_PC_KANA,
	SLIP.CUSTOMER_PC_PRE_CATEGORY,
	SLIP.CUSTOMER_TEL,
	SLIP.CUSTOMER_FAX,
	SLIP.CUSTOMER_EMAIL,
	SLIP.CUSTOMER_URL,
	SLIP.CUSTOMER_BUSINESS_CATEGORY,
	SLIP.CUSTOMER_JOB_CATEGORY,
	SLIP.CUSTOMER_RO_CATEGORY,
	SLIP.CUSTOMER_RANK_CATEGORY,
	SLIP.CUSTOMER_UPD_FLAG,
	SLIP.SALES_CM_CATEGORY,
	SLIP.TAX_SHIFT_CATEGORY,
	SLIP.RATE,
	SLIP.MAX_CREDIT_LIMIT,
	SLIP.LAST_CUTOFF_DATE,
	SLIP.CUTOFF_GROUP,
	SLIP.PAYBACK_TYPE_CATEGORY,
	SLIP.PAYBACK_CYCLE_CATEGORY,
	SLIP.TAX_FRACT_CATEGORY,
	SLIP.PRICE_FRACT_CATEGORY,
	SLIP.TEMP_DELIVERY_SLIP_FLAG,
	SLIP.PAYMENT_NAME,
	SLIP.REMARKS,
	SLIP.FIRST_SALES_DATE,
	SLIP.LAST_SALES_DATE,
	SLIP.SALES_PRICE_TOTAL,
	SLIP.SALES_PRICE_LSM,
	SLIP.COMMENT_DATA,
	SLIP.CRE_FUNC,
	SLIP.CRE_DATETM,
	SLIP.CRE_USER,
	SLIP.UPD_FUNC,
	SLIP.UPD_DATETM,
	SLIP.UPD_USER,
	SLIP.LAST_SALES_CUTOFF_DATE
FROM
	CUSTOMER_MST_HIST_/*$domainId*/ SLIP
/*BEGIN*/
WHERE
	/*IF recDateFrom != null */
	AND CAST(SLIP.REC_DATETM AS DATE) >= CAST(/*recDateFrom*/'2010/01/01' AS DATE)
	/*END*/
	/*IF recDateTo != null */
	AND CAST(SLIP.REC_DATETM AS DATE) <= CAST(/*recDateTo*/'2010/01/01' AS DATE)
	/*END*/
	/*IF customerCodeFrom != null */
	AND SLIP.CUSTOMER_CODE >= /*customerCodeFrom*/'S'
	/*END*/
	/*IF customerCodeTo != null */
	AND SLIP.CUSTOMER_CODE <= /*customerCodeTo*/'S'
	/*END*/
	/*IF creDateFrom != null */
	AND CAST(SLIP.CRE_DATETM AS DATE) >= CAST(/*creDateFrom*/'2010/01/01' AS DATE)
	/*END*/
	/*IF creDateTo != null */
	AND CAST(SLIP.CRE_DATETM AS DATE) <= CAST(/*creDateTo*/'2010/01/01' AS DATE)
	/*END*/
/*END*/
ORDER BY SLIP.HIST_ID
