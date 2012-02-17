SELECT
	COUNT(*)
FROM (
	SELECT
		WK.ONLINE_ORDER_ID,
		WK.SUPPLIER_DATE,
		WK.CUSTOMER_NAME,
		REL.RO_SLIP_ID
	FROM
		ONLINE_ORDER_WORK_/*$domainId*/ WK
		LEFT OUTER JOIN ONLINE_ORDER_REL_/*$domainId*/ REL
			ON WK.ONLINE_ORDER_ID = REL.ONLINE_ORDER_ID
				AND WK.ONLINE_ITEM_ID = REL.ONLINE_ITEM_ID
/*BEGIN*/
	WHERE
	/*IF userId != null */
		WK.USER_ID = /*userId*/'S'
	/*END*/
/*END*/
	GROUP BY
		WK.ONLINE_ORDER_ID,
		WK.SUPPLIER_DATE,
		WK.CUSTOMER_NAME,
		REL.RO_SLIP_ID
) WKREL
