SELECT
	R.WAREHOUSE_CODE
	,W.WAREHOUSE_NAME
	,R.RACK_CODE
	,R.RACK_NAME
	,R.RACK_CATEGORY
	,R.MULTI_FLAG
	,R.ZIP_CODE
	,R.ADDRESS_1
	,R.ADDRESS_2
	,R.RACK_PC_NAME
	,R.RACK_TEL
	,R.RACK_FAX
	,R.RACK_EMAIL
	,R.CRE_FUNC
	,R.CRE_DATETM
	,R.CRE_USER
	,R.UPD_FUNC
	,R.UPD_DATETM
	,R.UPD_USER
FROM
	RACK_MST_/*$domainId*/ R
	LEFT OUTER JOIN WAREHOUSE_MST_/*$domainId*/ W
    ON R.WAREHOUSE_CODE = W.WAREHOUSE_CODE
WHERE
	R.RACK_CODE=/*rackCode*/''