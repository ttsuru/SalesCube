SELECT
	C.CUSTOMER_CODE
	,C.CUSTOMER_NAME
	,C.CUSTOMER_KANA
	,C.CUSTOMER_OFFICE_NAME
	,C.CUSTOMER_OFFICE_KANA
	,C.CUSTOMER_ABBR

	,C.CUSTOMER_ZIP_CODE
	,C.CUSTOMER_ADDRESS_1
	,C.CUSTOMER_ADDRESS_2
	,C.CUSTOMER_PC_NAME
	,C.CUSTOMER_PC_KANA
	,CATT4.CATEGORY_CODE_NAME CUSTOMER_PC_PRE_CATEGORY_NM
	,C.CUSTOMER_DEPT_NAME
	,C.CUSTOMER_PC_POST
	,C.CUSTOMER_TEL
	,C.CUSTOMER_FAX
	,C.CUSTOMER_EMAIL

	,CR.RANK_NAME
	,CASE WHEN C.CUSTOMER_UPD_FLAG = '1' THEN '有'
          ELSE '無' END CUSTOMER_UPD_FLAG
	,CATT5.CATEGORY_CODE_NAME CUSTOMER_RO_CATEGORY_NM
	,C.MAX_CREDIT_LIMIT
	,CATT6.CATEGORY_CODE_NAME CUSTOMER_BUSINESS_CATEGORY_NM
	,CATT7.CATEGORY_CODE_NAME CUSTOMER_JOB_CATEGORY_NM
	,CATT8.CATEGORY_CODE_NAME TAX_FRACT_CATEGORY_NM
	,CATT9.CATEGORY_CODE_NAME PRICE_FRACT_CATEGORY_NM
	,CATT2.CATEGORY_CODE_NAME TAX_SHIFT_CATEGORY_NM
	,C.LAST_CUTOFF_DATE

	,CATT3.CATEGORY_CODE_NAME SALES_CM_CATEGORY_NM
	,CATT.CATEGORY_CODE_NAME
	,CATT10.CATEGORY_CODE_NAME PAYBACK_TYPE_CATEGORY_NM
	,CASE WHEN C.TEMP_DELIVERY_SLIP_FLAG = '0' THEN '不可'
          ELSE '可' END TEMP_DELIVERY_SLIP_FLAG

	,C.PAYMENT_NAME
	,C.REMARKS
	,C.COMMENT_DATA

	,DELIVER.DELIVERY_NAME DELIVER_DELIVERY_NAME
	,DELIVER.DELIVERY_KANA DELIVER_DELIVERY_KANA
	,DELIVER.DELIVERY_OFFICE_NAME DELIVER_DELIVERY_OFFICE_NAME
	,DELIVER.DELIVERY_OFFICE_KANA DELIVER_DELIVERY_OFFICE_KANA
	,DELIVER.DELIVERY_DEPT_NAME DELIVER_DELIVERY_DEPT_NAME
	,DELIVER.DELIVERY_ZIP_CODE DELIVER_DELIVERY_ZIP_CODE
	,DELIVER.DELIVERY_ADDRESS_1 DELIVER_DELIVERY_ADDRESS_1
	,DELIVER.DELIVERY_ADDRESS_2 DELIVER_DELIVERY_ADDRESS_2
	,DELIVER.DELIVERY_PC_NAME DELIVER_DELIVERY_PC_NAME
	,DELIVER.DELIVERY_PC_KANA DELIVER_DELIVERY_PC_KANA
	,CATT33.CATEGORY_CODE_NAME DELIVER_DELIVERY_PC_PRE_CATEGORY_NM
	,DELIVER.DELIVERY_TEL DELIVER_DELIVERY_TEL
	,DELIVER.DELIVERY_FAX DELIVER_DELIVERY_FAX
	,DELIVER.DELIVERY_EMAIL DELIVER_DELIVERY_EMAIL

	,BILL.DELIVERY_NAME BILL_DELIVERY_NAME
	,BILL.DELIVERY_KANA BILL_DELIVERY_KANA
	,BILL.DELIVERY_OFFICE_NAME BILL_DELIVERY_OFFICE_NAME
	,BILL.DELIVERY_OFFICE_KANA BILL_DELIVERY_OFFICE_KANA
	,BILL.DELIVERY_DEPT_NAME BILL_DELIVERY_DEPT_NAME
	,BILL.DELIVERY_ZIP_CODE BILL_DELIVERY_ZIP_CODE
	,BILL.DELIVERY_ADDRESS_1 BILL_DELIVERY_ADDRESS_1
	,BILL.DELIVERY_ADDRESS_2 BILL_DELIVERY_ADDRESS_2
	,BILL.DELIVERY_PC_NAME BILL_DELIVERY_PC_NAME
	,BILL.DELIVERY_PC_KANA BILL_DELIVERY_PC_KANA
	,CATT22.CATEGORY_CODE_NAME BILL_CATEGORY_CODE_NAME
	,BILL.DELIVERY_TEL BILL_DELIVERY_TEL
	,BILL.DELIVERY_FAX BILL_DELIVERY_FAX
	,BILL.DELIVERY_EMAIL BILL_DELIVERY_EMAIL

	,C.CRE_DATETM
	,C.UPD_DATETM
FROM
	CUSTOMER_MST_/*$domainId*/ C
		LEFT OUTER JOIN CUSTOMER_RANK_MST_/*$domainId*/ CR
			ON C.CUSTOMER_RANK_CATEGORY = CR.RANK_CODE
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT
			ON CONCAT(C.CUTOFF_GROUP, C.PAYBACK_CYCLE_CATEGORY) = CATT.CATEGORY_CODE AND CATT.CATEGORY_ID=11
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT2
			ON C.TAX_SHIFT_CATEGORY = CATT2.CATEGORY_CODE AND CATT2.CATEGORY_ID=29
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT3
			ON C.SALES_CM_CATEGORY = CATT3.CATEGORY_CODE AND CATT3.CATEGORY_ID=32
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT4
			ON C.CUSTOMER_PC_PRE_CATEGORY = CATT4.CATEGORY_CODE AND CATT4.CATEGORY_ID=10
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT5
			ON C.CUSTOMER_RO_CATEGORY = CATT5.CATEGORY_CODE AND CATT5.CATEGORY_ID=33
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT6
			ON C.CUSTOMER_BUSINESS_CATEGORY = CATT6.CATEGORY_CODE AND CATT6.CATEGORY_ID=41
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT7
			ON C.CUSTOMER_JOB_CATEGORY = CATT7.CATEGORY_CODE AND CATT7.CATEGORY_ID=42
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT8
			ON C.TAX_FRACT_CATEGORY = CATT8.CATEGORY_CODE AND CATT8.CATEGORY_ID=27
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT9
			ON C.PRICE_FRACT_CATEGORY = CATT9.CATEGORY_CODE AND CATT9.CATEGORY_ID=25
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT10
			ON C.PAYBACK_TYPE_CATEGORY = CATT10.CATEGORY_CODE AND CATT10.CATEGORY_ID=43
		LEFT OUTER JOIN CUSTOMER_REL_/*$domainId*/ R
			ON C.CUSTOMER_CODE = R.CUSTOMER_CODE
			AND           '01' = R.CUST_REL_CATEGORY
			AND (SELECT MAX(CRE_DATETM) FROM CUSTOMER_REL_/*$domainId*/ CRN WHERE C.CUSTOMER_CODE = CRN.CUSTOMER_CODE and CRN.CUST_REL_CATEGORY = '01') = R.CRE_DATETM
		INNER JOIN DELIVERY_MST_/*$domainId*/ DELIVER
			ON R.REL_CODE = DELIVER.DELIVERY_CODE
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT33
			ON DELIVER.DELIVERY_PC_PRE_CATEGORY = CATT33.CATEGORY_CODE
			AND 10=CATT33.CATEGORY_ID
		LEFT OUTER JOIN CUSTOMER_REL_/*$domainId*/ R2
			ON C.CUSTOMER_CODE = R2.CUSTOMER_CODE
			AND           '02' = R2.CUST_REL_CATEGORY
		INNER JOIN DELIVERY_MST_/*$domainId*/ BILL
			ON R2.REL_CODE = BILL.DELIVERY_CODE
		LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ CATT22
			ON BILL.DELIVERY_PC_PRE_CATEGORY = CATT22.CATEGORY_CODE
			AND 10=CATT22.CATEGORY_ID
/*BEGIN*/
WHERE
	/*IF customerCodeFrom != null */
	AND C.CUSTOMER_CODE >= /*customerCodeFrom*/'S'
	/*END*/
	/*IF customerCodeTo != null */
	AND C.CUSTOMER_CODE <= /*customerCodeTo*/'S'
	/*END*/
	/*IF creDateFrom != null */
	AND CAST(C.CRE_DATETM AS DATE) >= CAST(/*creDateFrom*/'2010/01/01' AS DATE)
	/*END*/
	/*IF creDateTo != null */
	AND CAST(C.CRE_DATETM AS DATE) <= CAST(/*creDateTo*/'2010/01/01' AS DATE)
	/*END*/
/*END*/
ORDER BY C.CUSTOMER_CODE ASC

