UPDATE
	PO_SLIP_TRN_/*$domainId*/
	SET
		PRICE_TOTAL = (
			SELECT TEMPVALUE FROM
			(
			SELECT
				CASE COALESCE(SLIPPRIFCATM.CATEGORY_CODE ,SUPPPRIFCATM.CATEGORY_CODE ,MINE.PRICE_FRACT_CATEGORY)
					WHEN /*roundDownId*/'0'	THEN TRUNCATE(PS.PRICE_TOTAL,0)
					WHEN /*halfUpId*/'1'	THEN ROUND(PS.PRICE_TOTAL,0)
					WHEN /*roundUpId*/'2'	THEN TRUNCATE(PS.PRICE_TOTAL + 0.9 * SIGN(PS.PRICE_TOTAL) ,0)
				ELSE PS.PRICE_TOTAL END AS TEMPVALUE
				FROM
					PO_SLIP_TRN_/*$domainId*/ PS
					INNER JOIN MINE_MST_/*$domainId*/ MINE
						ON COMPANY_NAME IS NOT NULL
					LEFT OUTER JOIN SUPPLIER_MST_/*$domainId*/ SUP
						ON SUP.SUPPLIER_CODE = PS.SUPPLIER_CODE
					LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ SUPPPRIFCATM
						ON SUPPPRIFCATM.CATEGORY_ID = /*priceFractCategoryId*/
						AND SUP.PRICE_FRACT_CATEGORY = SUPPPRIFCATM.CATEGORY_CODE
					LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ SLIPPRIFCATM
						ON SLIPPRIFCATM.CATEGORY_ID = /*priceFractCategoryId*/
						AND PS.PRICE_FRACT_CATEGORY = SLIPPRIFCATM.CATEGORY_CODE
				WHERE
					PO_SLIP_ID = /*poSlipId*/
			) TEMPTABLE
		)
		,CTAX_TOTAL = (
			SELECT TEMPVALUE FROM
			(
			SELECT
				CASE COALESCE(SLIPTAXFCATM.CATEGORY_CODE ,SUPPTAXFCATM.CATEGORY_CODE ,MINE.PRICE_FRACT_CATEGORY)
					WHEN /*roundDownId*/'0'	THEN TRUNCATE(PS.CTAX_TOTAL,0)
					WHEN /*halfUpId*/'1'	THEN ROUND(PS.CTAX_TOTAL,0)
					WHEN /*roundUpId*/'2'	THEN TRUNCATE(PS.CTAX_TOTAL + 0.9 * SIGN(PS.CTAX_TOTAL) ,0)
				ELSE PS.CTAX_TOTAL END AS TEMPVALUE
				FROM
					PO_SLIP_TRN_/*$domainId*/ PS
					INNER JOIN MINE_MST_/*$domainId*/ MINE
						ON COMPANY_NAME IS NOT NULL
					LEFT OUTER JOIN SUPPLIER_MST_/*$domainId*/ SUP
						ON SUP.SUPPLIER_CODE = PS.SUPPLIER_CODE
					LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ SUPPTAXFCATM
						ON SUPPTAXFCATM.CATEGORY_ID = /*taxFractCategoryId*/
						AND SUP.PRICE_FRACT_CATEGORY = SUPPTAXFCATM.CATEGORY_CODE
					LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ SLIPTAXFCATM
						ON SLIPTAXFCATM.CATEGORY_ID = /*taxFractCategoryId*/
						AND PS.PRICE_FRACT_CATEGORY = SLIPTAXFCATM.CATEGORY_CODE
				WHERE
					PO_SLIP_ID = /*poSlipId*/
			) TEMPTABLE
		)
		,FE_PRICE_TOTAL = (
			SELECT TEMPVALUE FROM
			(
			SELECT
				CASE WHEN MINE.UNIT_PRICE_DEC_ALIGNMENT >= 0 AND MINE.UNIT_PRICE_DEC_ALIGNMENT <= /*priceAlignMax*/ THEN
					CASE COALESCE(SLIPPRIFCATM.CATEGORY_CODE ,SUPPPRIFCATM.CATEGORY_CODE ,MINE.PRICE_FRACT_CATEGORY)
						WHEN /*roundDownId*/'0'	THEN TRUNCATE(PS.FE_PRICE_TOTAL,UNIT_PRICE_DEC_ALIGNMENT)
						WHEN /*halfUpId*/'1'	THEN ROUND(PS.FE_PRICE_TOTAL,UNIT_PRICE_DEC_ALIGNMENT)
						WHEN /*roundUpId*/'2'	THEN TRUNCATE(PS.FE_PRICE_TOTAL + 0.9 * POW(10,- MINE.UNIT_PRICE_DEC_ALIGNMENT) * SIGN(PS.FE_PRICE_TOTAL) ,MINE.UNIT_PRICE_DEC_ALIGNMENT)
					ELSE PS.FE_PRICE_TOTAL END
				ELSE PS.FE_PRICE_TOTAL END AS TEMPVALUE
				FROM
					PO_SLIP_TRN_/*$domainId*/ PS
					INNER JOIN MINE_MST_/*$domainId*/ MINE
						ON COMPANY_NAME IS NOT NULL
					LEFT OUTER JOIN SUPPLIER_MST_/*$domainId*/ SUP
						ON SUP.SUPPLIER_CODE = PS.SUPPLIER_CODE
					LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ SUPPPRIFCATM
						ON SUPPPRIFCATM.CATEGORY_ID = /*priceFractCategoryId*/
						AND SUP.PRICE_FRACT_CATEGORY = SUPPPRIFCATM.CATEGORY_CODE
					LEFT OUTER JOIN CATEGORY_TRN_/*$domainId*/ SLIPPRIFCATM
						ON SLIPPRIFCATM.CATEGORY_ID = /*priceFractCategoryId*/
						AND PS.PRICE_FRACT_CATEGORY = SLIPPRIFCATM.CATEGORY_CODE
				WHERE
					PO_SLIP_ID = /*poSlipId*/
			) TEMPTABLE
		)
	WHERE
		PO_SLIP_ID = /*poSlipId*/