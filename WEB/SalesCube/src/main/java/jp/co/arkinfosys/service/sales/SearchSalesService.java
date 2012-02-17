/*
 *  Copyright 2009-2010 Ark Information Systems.
 */

package jp.co.arkinfosys.service.sales;

import java.util.List;
import java.util.Map;

import jp.co.arkinfosys.common.Categories;
import jp.co.arkinfosys.common.Constants;
import jp.co.arkinfosys.common.StringUtil;
import jp.co.arkinfosys.entity.SalesSlipTrn;
import jp.co.arkinfosys.service.AbstractService;
import jp.co.arkinfosys.service.exception.ServiceException;

import org.seasar.framework.beans.util.BeanMap;

/**
 *
 * 売上伝票検索サービスクラスです.
 * @author Ark Information Systems
 *
 */
public class SearchSalesService extends AbstractService<SalesSlipTrn> {

	/**
	 * パラメータ定義クラスです.
	 */
	public static class Param {
		public static final String SEARCH_TARGET = "searchTarget";
		public static final String SALES_SLIP_ID = "salesSlipId";
		public static final String RO_SLIP_ID = "roSlipId";
		public static final String RECEPT_NO = "receptNo";
		public static final String SALES_DATE = "salesDate";
		public static final String SALES_DATE_FROM = "salesDateFrom";
		public static final String SALES_DATE_TO = "salesDateTo";
		public static final String DELIVERY_DATE_FROM = "deliveryDateFrom";
		public static final String DELIVERY_DATE_TO = "deliveryDateTo";
		public static final String DC_CATEGORY = "dcCategory";
		public static final String DC_TIMEZONE_CATEGORY = "dcTimezoneCategory";
		public static final String PICKING_REMARKS = "pickingRemarks";
		public static final String CUSTOMER_CODE = "customerCode";
		public static final String CUSTOMER_NAME = "customerName";
		public static final String CUSTOMER_PC_NAME = "customerPcName";
		public static final String SALES_CM_CATEGORY_LIST = "salesCmCategoryList";
		public static final String PRODUCT_CODE = "productCode";
		public static final String PRODUCT_ABSTRACT = "productAbstract";
		public static final String PRODUCT1 = "product1";
		public static final String PRODUCT2 = "product2";
		public static final String PRODUCT3 = "product3";
		public static final String SUPPLIER_CODE = "supplierCode";
		public static final String SUPPLIER_NAME = "supplierName";
		public static final String SORT_COLUMN = "sortColumn";
		public static final String SORT_ORDER_ASC = "sortOrderAsc";
		public static final String ROW_COUNT = "rowCount";
		public static final String OFFSET_ROW = "offsetRow";
		public static final String TAX_SHIFT_CATEGORY = "taxShiftCategory";
		public static final String CUTOFF_CATEGORY = "cutoffCategory";
		public static final String SALES_CM_CATEGORY = "salesCmCategory";
		public static final String DELIVERY_PROCESS_CATEGORY = "deliveryProcessCategory";

		public static final String SORT_COLUMN_SLIP = "sortColumnSlip";
		public static final String SORT_COLUMN_LINE = "sortColumnLine";
	}

	/**
	 *
	 * カラム定義クラスです.
	 *
	 */
	public static class Column {
		public static final String SALES_SLIP_ID = "SALES_SLIP_ID";
		public static final String RO_SLIP_ID = "RO_SLIP_ID";

		public static final String SORT_SALES_SLIP_ID = "SORT_SALES_SLIP_ID";
		public static final String SORT_SALES_LINE_NO = "SORT_SALES_LINE_NO";
		public static final String SORT_RO_SLIP_ID = "SORT_RO_SLIP_ID";
		public static final String SORT_RO_LINE_NO = "SORT_RO_LINE_NO";
	}

	/**
	 * 検索条件を指定して検索結果件数を取得します.
	 * @param params 検索条件
	 * @return 検索結果件数
	 * @throws ServiceException
	 */
	public Integer getSearchResultCount(BeanMap params) throws ServiceException {
		try {
			Integer count = Integer.valueOf(0);

			
			String searchTarget = (String)params.get(Param.SEARCH_TARGET);

			
			if(Constants.SEARCH_TARGET.VALUE_SLIP.equals(searchTarget)) {
				count = findSlipCntByCondition(params);
			} else if(Constants.SEARCH_TARGET.VALUE_LINE.equals(searchTarget)) {
				count = findSlipLineCntByCondition(params);
			}

			return count;
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 検索条件を指定して検索結果を取得します.
	 * @param params 検索条件
	 * @return 検索結果
	 * @throws ServiceException
	 */
	public List<BeanMap> getSearchResult(BeanMap params) throws ServiceException {
		try {
			
			String searchTarget = (String)params.get(Param.SEARCH_TARGET);

			
			if(Constants.SEARCH_TARGET.VALUE_SLIP.equals(searchTarget)) {
				return findSlipByCondition(params);
			} else if(Constants.SEARCH_TARGET.VALUE_LINE.equals(searchTarget)) {
				return findSlipLineByCondition(params);
			}

			return null;
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 検索条件を指定して伝票単位の検索結果件数を取得します.
	 * @param conditions 検索条件
	 * @return 検索結果件数
	 * @throws ServiceException
	 */
	public Integer findSlipCntByCondition(Map<String, Object> conditions)
			throws ServiceException {
		try {
			Map<String, Object> param = super.createSqlParam();
			setEmptyCondition(param);
			setConditionParam(conditions, param);

			return this.selectBySqlFile(Integer.class,
					"sales/FindSlipCntByCondition.sql", param).getSingleResult();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 検索条件を指定して明細行単位の検索結果件数を取得します.
	 * @param conditions 検索条件
	 * @return 検索結果件数
	 * @throws ServiceException
	 */
	public Integer findSlipLineCntByCondition(Map<String, Object> conditions)
			throws ServiceException {
		try {
			Map<String, Object> param = super.createSqlParam();
			setEmptyCondition(param);
			setConditionParam(conditions, param);

			return this.selectBySqlFile(Integer.class,
					"sales/FindSlipLineCntByCondition.sql", param)
					.getSingleResult();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 検索条件を指定して伝票単位の検索結果を取得します.
	 * @param conditions 検索条件
	 * @return 検索結果
	 * @throws ServiceException
	 */
	public List<BeanMap> findSlipByCondition(Map<String, Object> conditions)
			throws ServiceException {
		try {
			Map<String, Object> param = super.createSqlParam();
			setEmptyCondition(param);
			setConditionParam(conditions, param);

			return this.selectBySqlFile(BeanMap.class,
					"sales/FindSlipByCondition.sql", param).getResultList();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 検索条件を指定して明細行単位の検索結果を取得します.
	 * @param conditions 検索条件
	 * @return 検索結果
	 * @throws ServiceException
	 */
	public List<BeanMap> findSlipLineByCondition(Map<String, Object> conditions)
			throws ServiceException {
		try {
			Map<String, Object> param = super.createSqlParam();
			setEmptyCondition(param);
			setConditionParam(conditions, param);

			
			String sortColumn = (String)param.get(Param.SORT_COLUMN);
			if (Column.SALES_SLIP_ID.equals(sortColumn)) {
				
				param.put(Param.SORT_COLUMN_SLIP, Column.SORT_SALES_SLIP_ID);
				param.put(Param.SORT_COLUMN_LINE, Column.SORT_SALES_LINE_NO);
				param.put(Param.SORT_COLUMN, null);
			}else if(Column.RO_SLIP_ID.equals(sortColumn)){
				
				param.put(Param.SORT_COLUMN_SLIP, Column.SORT_RO_SLIP_ID);
				param.put(Param.SORT_COLUMN_LINE, Column.SORT_RO_LINE_NO);
				param.put(Param.SORT_COLUMN, null);
			}

			return this.selectBySqlFile(BeanMap.class,
					"sales/FindSlipLineByCondition.sql", param).getResultList();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 検索条件を受け取り、初期化して返します.
	 *
	 * @param param 検索条件オブジェクト
	 * @return 空の検索条件オブジェクト
	 */
	private Map<String, Object> setEmptyCondition(Map<String, Object> param) {
		param.put(Param.SEARCH_TARGET, null);
		param.put(Param.SALES_SLIP_ID, null);
		param.put(Param.RO_SLIP_ID, null);
		param.put(Param.RECEPT_NO, null);
		param.put(Param.SALES_DATE_FROM, null);
		param.put(Param.SALES_DATE_TO, null);
		param.put(Param.DELIVERY_DATE_FROM, null);
		param.put(Param.DELIVERY_DATE_TO, null);
		param.put(Param.DC_CATEGORY, null);
		param.put(Param.DC_TIMEZONE_CATEGORY, null);
		param.put(Param.PICKING_REMARKS, null);
		param.put(Param.CUSTOMER_CODE, null);
		param.put(Param.CUSTOMER_NAME, null);
		param.put(Param.CUSTOMER_PC_NAME, null);
		param.put(Param.SALES_CM_CATEGORY_LIST, null);
		param.put(Param.PRODUCT_CODE, null);
		param.put(Param.PRODUCT_ABSTRACT, null);
		param.put(Param.PRODUCT1, null);
		param.put(Param.PRODUCT2, null);
		param.put(Param.PRODUCT3, null);
		param.put(Param.SUPPLIER_CODE, null);
		param.put(Param.SUPPLIER_NAME, null);
		param.put(Param.SORT_COLUMN, null);
		param.put(Param.SORT_ORDER_ASC, null);
		param.put(Param.ROW_COUNT, null);
		param.put(Param.OFFSET_ROW, null);
		param.put(Param.TAX_SHIFT_CATEGORY, Integer.valueOf(Categories.ART_TAX_SHIFT_CATEGORY));
		param.put(Param.CUTOFF_CATEGORY, Integer.valueOf(Categories.CUTOFF_GROUP));
		param.put(Param.SALES_CM_CATEGORY, Integer.valueOf(Categories.SALES_CM_CATEGORY));
		param.put(Param.DELIVERY_PROCESS_CATEGORY, Integer.valueOf(Categories.DELIVERY_PROCESS_CATEGORY));
		param.put(Param.SORT_COLUMN_SLIP,null);
		param.put(Param.SORT_COLUMN_LINE,null);
		return param;
	}

	/**
	 * 検索条件パラメータを設定して返します.
	 * @param conditions 検索条件
	 * @param param 検索条件パラメータ
	 * @return 検索条件が設定された検索条件パラメータ
	 */
	private Map<String, Object> setConditionParam(
			Map<String, Object> conditions, Map<String, Object> param) {

		
		if (conditions.containsKey(Param.SALES_SLIP_ID)) {
			if (StringUtil.hasLength((String)conditions.get(Param.SALES_SLIP_ID))) {
				param.put(Param.SALES_SLIP_ID,new Long((String)conditions.get(Param.SALES_SLIP_ID)));
			}
		}

		
		if (conditions.containsKey(Param.RO_SLIP_ID)) {
			if (StringUtil.hasLength((String)conditions.get(Param.RO_SLIP_ID))) {
				param.put(Param.RO_SLIP_ID,new Long((String)conditions.get(Param.RO_SLIP_ID)));
			}
		}

		
		if (conditions.containsKey(Param.RECEPT_NO)) {
			if (StringUtil.hasLength((String)conditions.get(Param.RECEPT_NO))) {
				param.put(Param.RECEPT_NO,super.createPrefixSearchCondition((String)conditions.get(Param.RECEPT_NO)));
			}
		}

		
		if (conditions.containsKey(Param.SALES_DATE_FROM)) {
			if (StringUtil.hasLength((String)conditions.get(Param.SALES_DATE_FROM))) {
				param.put(Param.SALES_DATE_FROM,(String)conditions.get(Param.SALES_DATE_FROM));
			}
		}

		
		if (conditions.containsKey(Param.SALES_DATE_TO)) {
			if (StringUtil.hasLength((String)conditions.get(Param.SALES_DATE_TO))) {
				param.put(Param.SALES_DATE_TO,(String)conditions.get(Param.SALES_DATE_TO));
			}
		}

		
		if (conditions.containsKey(Param.DELIVERY_DATE_FROM)) {
			if (StringUtil.hasLength((String)conditions.get(Param.DELIVERY_DATE_FROM))) {
				param.put(Param.DELIVERY_DATE_FROM,(String)conditions.get(Param.DELIVERY_DATE_FROM));
			}
		}

		
		if (conditions.containsKey(Param.DELIVERY_DATE_TO)) {
			if (StringUtil.hasLength((String)conditions.get(Param.DELIVERY_DATE_TO))) {
				param.put(Param.DELIVERY_DATE_TO,(String)conditions.get(Param.DELIVERY_DATE_TO));
			}
		}

		
		if (conditions.containsKey(Param.DC_CATEGORY)) {
			if (StringUtil.hasLength((String)conditions.get(Param.DC_CATEGORY))) {
				param.put(Param.DC_CATEGORY,(String)conditions.get(Param.DC_CATEGORY));
			}
		}

		
		if (conditions.containsKey(Param.DC_TIMEZONE_CATEGORY)) {
			if (StringUtil.hasLength((String)conditions.get(Param.DC_TIMEZONE_CATEGORY))) {
				param.put(Param.DC_TIMEZONE_CATEGORY,(String)conditions.get(Param.DC_TIMEZONE_CATEGORY));
			}
		}

		
		if (conditions.containsKey(Param.PICKING_REMARKS)) {
			if (StringUtil.hasLength((String)conditions.get(Param.PICKING_REMARKS))) {
				param.put(Param.PICKING_REMARKS,super.createPartialSearchCondition((String)conditions.get(Param.PICKING_REMARKS)));
			}
		}

		
		if (conditions.containsKey(Param.CUSTOMER_CODE)) {
			if (StringUtil.hasLength((String)conditions.get(Param.CUSTOMER_CODE))) {
				param.put(Param.CUSTOMER_CODE,super.createPrefixSearchCondition((String)conditions.get(Param.CUSTOMER_CODE)));
			}
		}

		
		if (conditions.containsKey(Param.CUSTOMER_NAME)) {
			if (StringUtil.hasLength((String)conditions.get(Param.CUSTOMER_NAME))) {
				param.put(Param.CUSTOMER_NAME,super.createPartialSearchCondition((String)conditions.get(Param.CUSTOMER_NAME)));
			}
		}

		
		if (conditions.containsKey(Param.CUSTOMER_PC_NAME)) {
			if (StringUtil.hasLength((String)conditions.get(Param.CUSTOMER_PC_NAME))) {
				param.put(Param.CUSTOMER_PC_NAME,super.createPartialSearchCondition((String)conditions.get(Param.CUSTOMER_PC_NAME)));
			}
		}

		
		if(conditions.containsKey(Param.SALES_CM_CATEGORY_LIST)) {
			param.put(Param.SALES_CM_CATEGORY_LIST, conditions.get(Param.SALES_CM_CATEGORY_LIST));
		}

		
		if (conditions.containsKey(Param.PRODUCT_CODE)) {
			if (StringUtil.hasLength((String)conditions.get(Param.PRODUCT_CODE))) {
				param.put(Param.PRODUCT_CODE,
					super.createPrefixSearchCondition((String)conditions.get(Param.PRODUCT_CODE)));
			}
		}

		
		if (conditions.containsKey(Param.PRODUCT_ABSTRACT)) {
			if (StringUtil.hasLength((String)conditions.get(Param.PRODUCT_ABSTRACT))) {
				param.put(Param.PRODUCT_ABSTRACT,
					super.createPartialSearchCondition((String)conditions.get(Param.PRODUCT_ABSTRACT)));
			}
		}

		
		if (conditions.containsKey(Param.PRODUCT1)) {
			if (StringUtil.hasLength((String)conditions.get(Param.PRODUCT1))) {
				param.put(Param.PRODUCT1,(String)conditions.get(Param.PRODUCT1));
			}
		}

		
		if (conditions.containsKey(Param.PRODUCT2)) {
			if (StringUtil.hasLength((String)conditions.get(Param.PRODUCT2))) {
				param.put(Param.PRODUCT2,(String)conditions.get(Param.PRODUCT2));
			}
		}

		
		if (conditions.containsKey(Param.PRODUCT3)) {
			if (StringUtil.hasLength((String)conditions.get(Param.PRODUCT3))) {
				param.put(Param.PRODUCT3,(String)conditions.get(Param.PRODUCT3));
			}
		}

		
		if (conditions.containsKey(Param.SUPPLIER_CODE)) {
			if (StringUtil.hasLength((String)conditions.get(Param.SUPPLIER_CODE))) {
				param.put(Param.SUPPLIER_CODE,
					super.createPrefixSearchCondition((String)conditions.get(Param.SUPPLIER_CODE)));
			}
		}

		
		if (conditions.containsKey(Param.SUPPLIER_NAME)) {
			if (StringUtil.hasLength((String)conditions.get(Param.SUPPLIER_NAME))) {
				param.put(Param.SUPPLIER_NAME,
					super.createPartialSearchCondition((String)conditions.get(Param.SUPPLIER_NAME)));
			}
		}

		
		if (conditions.containsKey(Param.SORT_COLUMN)) {
			if (StringUtil.hasLength((String)conditions.get(Param.SORT_COLUMN))) {
				param.put(Param.SORT_COLUMN,
						StringUtil.convertColumnName((String)conditions.get(Param.SORT_COLUMN)));
			}
		}

		
		Boolean sortOrderAsc = (Boolean)conditions.get(Param.SORT_ORDER_ASC);
		if (sortOrderAsc) {
			param.put(Param.SORT_ORDER_ASC, Constants.SQL.ASC);
		} else {
			param.put(Param.SORT_ORDER_ASC, Constants.SQL.DESC);
		}

		
		if (conditions.containsKey(Param.ROW_COUNT)) {
			param.put(Param.ROW_COUNT,
					conditions.get(Param.ROW_COUNT));
		}

		
		if (conditions.containsKey(Param.OFFSET_ROW)) {
			param.put(Param.OFFSET_ROW,conditions.get(Param.OFFSET_ROW));
		}

		return param;
	}
}
