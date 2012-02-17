SELECT
	EEL.PRODUCT_CODE,
	IFNULL(SUM(EEL.QUANTITY), 0) AS QUANTITY
FROM
	ENTRUST_EAD_SLIP_TRN_/*$domainId*/ EES
	INNER JOIN ENTRUST_EAD_LINE_TRN_/*$domainId*/ EEL ON EES.ENTRUST_EAD_SLIP_ID = EEL.ENTRUST_EAD_SLIP_ID
WHERE
	EEL.PRODUCT_CODE = /*productCode*/
	AND EEL.ENTRUST_EAD_CATEGORY = '1' AND EEL.REL_ENTRUST_EAD_LINE_ID IS NULL
	AND EES.ENTRUST_EAD_DATE <= CURDATE()
GROUP BY
	EEL.PRODUCT_CODE