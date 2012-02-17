INSERT INTO
    SUPPLIER_SLIP_TRN_/*$domainId*/
(
	SUPPLIER_SLIP_ID
	,STATUS
	,SUPPLIER_DATE
	,SUPPLIER_ANNUAL
	,SUPPLIER_MONTHLY
	,SUPPLIER_YM
	,USER_ID
	,USER_NAME
	,SUPPLIER_SLIP_CATEGORY
	,SUPPLIER_CODE
	,SUPPLIER_NAME
	,SUPPLIER_CM_CATEGORY
	,DELIVERY_DATE
	,RATE_ID
	,TAX_SHIFT_CATEGORY
	,TAX_FRACT_CATEGORY
	,PRICE_FRACT_CATEGORY
	,CTAX_TOTAL
	,PRICE_TOTAL
	,FE_PRICE_TOTAL
	,PO_SLIP_ID
	,PAYMENT_SLIP_ID
	,SUPPLIER_PAYMENT_DATE
	,PAYMENT_CUTOFF_DATE
	,PAYMENT_PDATE
	,REMARKS
	,CRE_FUNC
	,CRE_DATETM
	,CRE_USER
	,UPD_FUNC
	,UPD_DATETM
	,UPD_USER
)
VALUES
(
	/*supplierSlipId*/NULL
	,/*status*/NULL
	,/*supplierDate*/NULL
	,/*supplierAnnual*/NULL
	,/*supplierMonthly*/NULL
	,/*supplierYm*/NULL
	,/*userId*/NULL
	,/*userName*/NULL
	,/*supplierSlipCategory*/NULL
	,/*supplierCode*/NULL
	,/*supplierName*/NULL
	,/*supplierCmCategory*/NULL
	,/*deliveryDate*/NULL
	,/*rateId*/NULL
	,/*taxShiftCategory*/NULL
	,/*taxFractCategory*/NULL
	,/*priceFractCategory*/NULL
	,/*ctaxTotal*/NULL
	,/*priceTotal*/NULL
	,/*fePriceTotal*/NULL
	,/*poSlipId*/NULL
	,/*paymentSlipId*/NULL
	,/*supplierPaymentDate*/NULL
	,/*paymentCutoffDate*/NULL
	,/*paymentPdate*/NULL
	,/*remarks*/NULL
	,/*creFunc*/
	,now()
	,/*creUser*/
	,/*updFunc*/
	,now()
	,/*updUser*/
);
