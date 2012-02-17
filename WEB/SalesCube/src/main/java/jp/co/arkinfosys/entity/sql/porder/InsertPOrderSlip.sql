INSERT
    INTO
        PO_SLIP_TRN_/*$domainId*/ (
			PO_SLIP_ID
			,STATUS
			,PO_DATE
			,PO_ANNUAL
			,PO_MONTHLY
			,PO_YM
			,DELIVERY_DATE
			,USER_ID
			,USER_NAME
			,REMARKS
			,SUPPLIER_CODE
			,SUPPLIER_NAME
			,SUPPLIER_KANA
			,SUPPLIER_ZIP_CODE
			,SUPPLIER_ADDRESS_1
			,SUPPLIER_ADDRESS_2
			,SUPPLIER_PC_NAME
			,SUPPLIER_PC_KANA
			,SUPPLIER_PC_PRE_CATEGORY
			,SUPPLIER_PC_POST
			,SUPPLIER_TEL
			,SUPPLIER_FAX
			,SUPPLIER_EMAIL
			,SUPPLIER_URL
			,TRANSPORT_CATEGORY
			,TAX_SHIFT_CATEGORY
			,TAX_FRACT_CATEGORY
			,PRICE_FRACT_CATEGORY
			,RATE_ID
			,SUPPLIER_CM_CATEGORY
			,PRICE_TOTAL
			,CTAX_TOTAL
			,FE_PRICE_TOTAL
			,PRINT_COUNT
			,CRE_FUNC
			,CRE_DATETM
			,CRE_USER
			,UPD_FUNC
			,UPD_DATETM
			,UPD_USER

			,SUPPLIER_ABBR
			,SUPPLIER_DEPT_NAME
			,SUPPLIER_PC_PRE
)
	SELECT
			/*poSlipId*/0
			,0
			,/*poDate*/NULL
			,/*poAnnual*/NULL
			,/*poMonthly*/NULL
			,/*poYm*/NULL
			,/*deliveryDate*/NULL
			,/*userId*/NULL
			,/*userName*/NULL
			,/*remarks*/NULL
			,/*supplierCode*/NULL
			,/*supplierName*/NULL
			,/*supplierKana*/NULL
			,/*supplierZipCode*/NULL
			,/*supplierAddress1*/NULL
			,/*supplierAddress2*/NULL
			,/*supplierPcName*/NULL
			,/*supplierPcKana*/NULL
			,/*supplierPcPreCategory*/NULL
			,/*supplierPcPost*/NULL
			,/*supplierTel*/NULL
			,/*supplierFax*/NULL
			,/*supplierEmail*/NULL
			,SUPPMST.SUPPLIER_URL
			,/*transportCategory*/NULL
			,SUPPMST.TAX_SHIFT_CATEGORY
			,/*taxFractCategory*/NULL
			,/*priceFractCategory*/NULL
			,/*rateId*/NULL
			,SUPPMST.SUPPLIER_CM_CATEGORY
			,/*priceTotal*/0
			,/*ctaxTotal*/0
			,/*fePriceTotal*/0
			,0
			,/*creFunc*/NULL
			,now()
			,/*creUser*/NULL
			,/*updFunc*/NULL
			,now()
			,/*updUser*/NULL

			,/*supplierAbbr*/
			,/*supplierDeptName*/
			,/*supplierPcPre*/
	FROM SUPPLIER_MST_/*$domainId*/ SUPPMST
		WHERE SUPPMST.SUPPLIER_CODE = /*supplierCode*/''
