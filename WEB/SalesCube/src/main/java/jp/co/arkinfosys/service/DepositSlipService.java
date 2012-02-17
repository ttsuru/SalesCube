/*
 *  Copyright 2009-2010 Ark Information Systems.
 */

package jp.co.arkinfosys.service;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.co.arkinfosys.common.CategoryTrns;
import jp.co.arkinfosys.common.Constants;
import jp.co.arkinfosys.common.StringUtil;
import jp.co.arkinfosys.dto.deposit.DepositLineDto;
import jp.co.arkinfosys.dto.deposit.DepositSlipDto;
import jp.co.arkinfosys.dto.sales.SalesSlipDto;
import jp.co.arkinfosys.entity.DepositLine;
import jp.co.arkinfosys.entity.DepositSlip;
import jp.co.arkinfosys.entity.join.DeliveryAndPre;
import jp.co.arkinfosys.service.exception.ServiceException;
import jp.co.arkinfosys.service.exception.UnabledLockException;

import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.util.MessageResourcesUtil;

/**
 * 入金伝票サービスクラスです.
 * @author Ark Information Systems
 *
 */
public class DepositSlipService extends
		AbstractSlipService<DepositSlip, DepositSlipDto> {

	
	public SeqMakerService seqMakerService;

	
	@Resource
	private DepositLineService depositLineService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private DeliveryService deliveryService;

	/**
	 * 日付の形式指定
	 */
	SimpleDateFormat DF_YMD = new SimpleDateFormat(Constants.FORMAT.DATE);

	/**
	 * パラメータマッピングクラスです.
	 * @author Ark Information Systems
	 *
	 */
	public static class Param {
		private static final String SORT_ORDER = "sortOrder"; 
		private static final String ROW_COUNT = "rowCount"; 
		private static final String OFFSET_ROW = "offsetRow"; 
		public static final String DEPOSIT_SLIP_ID = "depositSlipId"; 
		public static final String CUSTOMER_CODE = "customerCode"; 
		public static final String DEPOSIT_DATE = "depositDate"; 
		public static final String DEPOSIT_DATE_FROM = "depositDateFrom"; 
		public static final String DEPOSIT_DATE_TO = "depositDateTo"; 
		private static final String SORT_COLUMN_DEPOSIT_DATE = "sortColumnDepositDate"; 
		private static final String STATUS = "status"; 
		private static final String BILL_CUTOFF_DATE = "billCutoffDate"; 
		private static final String SALES_SLIP_ID = "salesSlipId"; 
		public static final String IS_CONTAIN_CLOSE_LEAK = "isContainCloseLeak";
		public static final String LEAK_CHECK_CUTOFF_DATE = "leakCheckCutoffDate";
		public static final String DEPOSIT_CATEGORY = "depositCategory";
		private static final String SALES_CUTOFF_DATE = "salesCutoffDate"; 

	}

	public String[] params = { Param.SORT_ORDER, Param.ROW_COUNT,
			Param.OFFSET_ROW, Param.DEPOSIT_SLIP_ID, Param.CUSTOMER_CODE,
			Param.DEPOSIT_DATE, Param.DEPOSIT_DATE_FROM, Param.DEPOSIT_DATE_TO,
			Param.SORT_COLUMN_DEPOSIT_DATE, Param.STATUS,
			Param.BILL_CUTOFF_DATE, Param.SALES_SLIP_ID,
			Param.IS_CONTAIN_CLOSE_LEAK, Param.LEAK_CHECK_CUTOFF_DATE,
			Param.DEPOSIT_CATEGORY, Param.SALES_CUTOFF_DATE };

	/**
	 * 納入先コードのカラム名
	 */
	private static final String COLUMN_DEPOSIT_DATE = "DEPOSIT_DATE";

	/**
	 * 請求締処理の伝票設定を行います.
	 * @param dto 入金伝票DTO
	 * @param billId 請求書番号
	 * @param lastCutOffDate 請求締日の文字列
	 * @param cutoffPdate 請求締処理日
	 * @throws ParseException
	 */
	protected void setCloseDepositSlipBill(DepositSlipDto dto, Integer billId,
			String lastCutOffDate, Timestamp cutoffPdate) throws ParseException {

		
		dto.status = DepositSlip.STATUS_CLOSE;
		
		dto.billId = billId.toString();
		
		dto.billCutoffDate = super.convertUtilDateToSqlDate(
				DF_YMD.parse(lastCutOffDate)).toString();
		
		dto.billCutoffPdate = cutoffPdate.toString();
	}

	/**
	 * 請求締解除処理の伝票設定を行います.
	 * @param ds 入金伝票DTO
	 * @throws ParseException
	 */
	protected void setReOpenDepositSlipBill(DepositSlipDto ds)
			throws ParseException {
	}

	/**
	 * 売掛締処理の伝票設定を行います.
	 * @param ds 入金伝票DTO
	 * @param artBalanceId 売掛残高番号
	 * @param lastCutOffDate 請求締日の文字列
	 * @param cutoffPdate 請求締処理日
	 * @throws ParseException
	 */
	protected void setCloseDepositSlipArt(DepositSlipDto ds,
			Integer artBalanceId, String lastCutOffDate, Timestamp cutoffPdate)
			throws ParseException {

		
		if (!isCreditType(ds)) {
			ds.status = DepositSlip.STATUS_CLOSE;
		}
		
		ds.artId = artBalanceId.toString();
		
		ds.salesCutoffDate = lastCutOffDate;
		
		ds.salesCutoffPdate = new SimpleDateFormat(Constants.FORMAT.TIMESTAMP).format(cutoffPdate);
	}

	/**
	 * 売掛締解除処理の伝票設定を行います.
	 * @param ds 入金伝票DTO
	 * @throws ParseException
	 */
	protected void setReOpenDepositSlipArt(DepositSlipDto ds)
			throws ParseException {

		
		if (!isCreditType(ds)) {
			ds.status = DepositSlip.STATUS_INIT;
		}
		
		ds.artId = null;
		
		ds.salesCutoffDate = null;
		
		ds.salesCutoffPdate = null;
	}

	/**
	 * 顧客コードを指定して、発効日が指定日付より後の入金伝票エンティティのリストを返します.
	 * @param customerCode 顧客コード
	 * @param startDate 指定日時
	 * @return 入金伝票エンティティのリスト
	 * @throws ServiceException
	 */
	public List<DepositSlip> findDepositSlipByCustomerCodeAndDate(
			String customerCode, Date startDate) throws ServiceException {

		LinkedHashMap<String, Object> conditions = new LinkedHashMap<String, Object>();

		
		
		conditions.put(Param.CUSTOMER_CODE, customerCode);
		if (startDate != null) {
			conditions.put(Param.DEPOSIT_DATE, DF_YMD.format(startDate));
		}
		conditions.put(Param.SORT_COLUMN_DEPOSIT_DATE, COLUMN_DEPOSIT_DATE);
		conditions.put(Param.SORT_ORDER, "DESC");

		return findByCondition(conditions, params,
				"deposit/FindDepositSlip.sql");

	}

	/**
	 * 顧客コードを指定して、発行日が指定日付より前かつ未請求締めの入金伝票DTOのリストを返します.
	 * @param customerCode 顧客コード
	 * @param closeDate 締処理日付
	 * @param depositCategory 入金区分
	 * @return 入金伝票DTOのリスト
	 * @throws ServiceException
	 */
	public List<DepositSlipDto> findOpenDepositSlipByCustomerCode(
			String customerCode, String closeDate, String depositCategory)
			throws ServiceException {

		LinkedHashMap<String, Object> conditions = new LinkedHashMap<String, Object>();

		
		
		conditions.put(Param.STATUS, DepositSlip.STATUS_INIT);
		conditions.put(Param.CUSTOMER_CODE, customerCode);
		conditions.put(Param.DEPOSIT_DATE, closeDate);
		conditions.put(Param.DEPOSIT_CATEGORY, depositCategory);
		conditions.put(Param.SORT_COLUMN_DEPOSIT_DATE, COLUMN_DEPOSIT_DATE);
		conditions.put(Param.SORT_ORDER, "DESC");

		List<DepositSlipDto> result = new ArrayList<DepositSlipDto>();
		List<DepositSlip> l = findByCondition(conditions, params,
				"deposit/FindOpenDepositSlip.sql");
		for (DepositSlip ds : l) {
			result.add(this.createAndCopy(ds));
		}
		return result;
	}

	/**
	 * 顧客コードを指定して、発行日が指定日付より前かつ未売掛締めの入金伝票DTOのリストを返します.
	 * @param customerCode 顧客コード
	 * @param closeDate 締処理日付
	 * @return 入金伝票DTOのリスト
	 * @throws ServiceException
	 */
	public List<DepositSlipDto> findArtOpenDepositSlipByCustomerCode(
			String customerCode, String closeDate) throws ServiceException {

		LinkedHashMap<String, Object> conditions = new LinkedHashMap<String, Object>();

		
		
		conditions.put(Param.CUSTOMER_CODE, customerCode);
		conditions.put(Param.DEPOSIT_DATE, closeDate);
		conditions.put(Param.SALES_CUTOFF_DATE, null);
		conditions.put(Param.SORT_COLUMN_DEPOSIT_DATE, COLUMN_DEPOSIT_DATE);
		conditions.put(Param.SORT_ORDER, "DESC");

		List<DepositSlip> l = findByCondition(conditions, params,
				"deposit/FindArtOpenDepositSlip.sql");
		List<DepositSlipDto> result = new ArrayList<DepositSlipDto>();

		for (DepositSlip ds : l) {
			result.add(this.createAndCopy(ds));
		}

		return result;
	}

	/**
	 * 顧客コードを指定して、指定請求締日に締めた入金伝票DTOのリストを返します.
	 * @param customerCode 顧客コード
	 * @param lastCutOffDate 請求締日
	 * @param depositCategory 入金区分
	 * @return 入金伝票DTOのリスト
	 * @throws ServiceException
	 */
	public List<DepositSlipDto> findCloseDepositSlipByCustomerCode(
			String customerCode, String lastCutOffDate, String depositCategory)
			throws ServiceException {

		LinkedHashMap<String, Object> conditions = new LinkedHashMap<String, Object>();

		
		
		conditions.put(Param.STATUS, DepositSlip.STATUS_CLOSE);
		conditions.put(Param.CUSTOMER_CODE, customerCode);
		conditions.put(Param.BILL_CUTOFF_DATE, lastCutOffDate);
		conditions.put(Param.DEPOSIT_CATEGORY, depositCategory);
		conditions.put(Param.SORT_COLUMN_DEPOSIT_DATE, COLUMN_DEPOSIT_DATE);
		conditions.put(Param.SORT_ORDER, "DESC");

		List<DepositSlipDto> result = new ArrayList<DepositSlipDto>();

		List<DepositSlip> l = findByCondition(conditions, params,
				"deposit/FindOpenDepositSlip.sql");
		for (DepositSlip ds : l) {
			result.add(this.createAndCopy(ds));
		}

		return result;

	}

	/**
	 * 顧客コードを指定して、指定売掛締日に締めた入金伝票DTOのリストを返します.
	 * @param customerCode 顧客コード
	 * @param lastCutOffDate 請求締日
	 * @return 入金伝票DTOのリスト
	 * @throws ServiceException
	 */
	public List<DepositSlipDto> findArtCloseDepositSlipByCustomerCode(
			String customerCode, String lastCutOffDate) throws ServiceException {

		LinkedHashMap<String, Object> conditions = new LinkedHashMap<String, Object>();

		
		
		conditions.put(Param.CUSTOMER_CODE, customerCode);
		conditions.put(Param.SALES_CUTOFF_DATE, lastCutOffDate);
		conditions.put(Param.SORT_COLUMN_DEPOSIT_DATE, COLUMN_DEPOSIT_DATE);
		conditions.put(Param.SORT_ORDER, "DESC");

		List<DepositSlip> l = findByCondition(conditions, params,
				"deposit/FindArtOpenDepositSlip.sql");
		List<DepositSlipDto> result = new ArrayList<DepositSlipDto>();
		for (DepositSlip ds : l) {
			result.add(this.createAndCopy(ds));
		}

		return result;
	}

	/**
	 * 入金伝票IDを指定して、入金伝票エンティティのリストを返します.
	 * @param depositSlipId 入金伝票ID
	 * @return 入金伝票エンティティのリスト
	 * @throws ServiceException
	 */
	public List<DepositSlip> findDepositSlipBySlipId(String depositSlipId)
			throws ServiceException {

		LinkedHashMap<String, Object> conditions = new LinkedHashMap<String, Object>();

		
		
		conditions.put(Param.DEPOSIT_SLIP_ID, depositSlipId);

		return findByCondition(conditions, params,
				"deposit/FindDepositSlip.sql");

	}

	/**
	 * 今回回収額(指定した伝票以外で前回締め以降に発生した入金伝票の伝票合計)を返します.
	 * @param customerCode 顧客コード
	 * @param startDate 指定日付
	 * @param depositSlipId 入金伝票ID
	 * @return 回収額
	 * @throws ServiceException
	 */
	public BigDecimal getDepositTotalPrice(String customerCode, Date startDate,
			String depositSlipId) throws ServiceException {

		List<DepositSlip> depositList = findDepositSlipByCustomerCodeAndDate(
				customerCode, startDate);

		Double depositTotal = 0.0;

		
		Integer dsId;
		if (!StringUtil.hasLength(depositSlipId)) {
			dsId = -1;
		} else {
			dsId = Integer.parseInt(depositSlipId);
		}

		
		for (DepositSlip ds : depositList) {
			
			if (ds.depositSlipId.equals(dsId) == true) {
				continue;
			}
			if (ds.depositTotal != null) {
				depositTotal += ds.depositTotal.doubleValue();
			}
		}
		return new BigDecimal(depositTotal);
	}

	/**
	 * エンティティからDTOへの変換を行います.
	 * @param ds 入金伝票エンティティ
	 * @return 入金伝票DTO
	 */
	private DepositSlipDto createAndCopy(DepositSlip ds) {
		return Beans.createAndCopy(DepositSlipDto.class, ds).dateConverter(Constants.FORMAT.DATE,
				"depositDate", "inputPdate", "billCutoffDate").dateConverter(
				Constants.FORMAT.TIMESTAMP, "billCutoffPdate", "creDatetm",
				"updDatetm").execute();
	}

	/**
	 * DTOからエンティティへの変換を行います.
	 * @param ds 入金伝票DTO
	 * @return 入金伝票エンティティ
	 */
	private DepositSlip createAndCopy(DepositSlipDto ds) {
		return Beans.createAndCopy(DepositSlip.class, ds).dateConverter(Constants.FORMAT.DATE,
				"depositDate", "inputPdate", "billCutoffDate").dateConverter(
				Constants.FORMAT.TIMESTAMP, "billCutoffPdate", "creDatetm",
				"updDatetm").execute();
	}

	/**
	 * エンティティ内容をDTOにコピーします.
	 * @param ds 入金伝票エンティティ
	 * @param dto 入金伝票DTO
	 */
	private void copy(DepositSlip ds, DepositSlipDto dto) {
		
		Beans.copy(ds, dto).dateConverter(
				Constants.FORMAT.DATE, "depositDate", "inputPdate",
				"billCutoffDate").dateConverter(Constants.FORMAT.TIMESTAMP,
				"billCutoffPdate", "creDatetm", "updDatetm").execute();
	}

	/**
	 * パラメータマップを作成します.
	 * @param dsd 入金伝票DTO
	 * @return パラメータマップ
	 */
	private Map<String, Object> createParamMap(DepositSlipDto dsd){

		
		Map<String, Object> param = new HashMap<String, Object>();

		
		BeanMap AFparam = Beans.createAndCopy(BeanMap.class,dsd)
			.numberConverter("#","depositTotal")
			.execute();
		param.putAll(AFparam);

		
		Map<String, Object> CommonParam = super.createSqlParam();
		param.putAll(CommonParam);

		return param;
	}

	/**
	 * 請求締処理で入金伝票を締めます.
	 * @param depositSlipList 入金伝票DTOのリスト
	 * @param billId 請求書番号
	 * @param lastCutOffDate 請求締日の文字列
	 * @param cutoffPdate 請求締処理日
	 * @return 更新した件数
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @throws ParseException
	 * @throws SQLException
	 * @throws GeneralSecurityException
	 */
	public int closeDepositSlipBill(List<DepositSlipDto> depositSlipList,
			Integer billId, String lastCutOffDate, Timestamp cutoffPdate)
			throws ServiceException, UnabledLockException, ParseException,
			SQLException, GeneralSecurityException {

		int updateCount = 0;
		for (DepositSlipDto ds : depositSlipList) {
			
			this.lockRecord(Param.DEPOSIT_SLIP_ID, ds.depositSlipId,
					ds.updDatetm, "deposit/LockSlip.sql");

			
			List<DepositLineDto> dlList = depositLineService.loadBySlip(ds);

			
			for (DepositLineDto dl : dlList) {
				
				dl.status = DepositLine.STATUS_CLOSE;
				if (depositLineService.updateRecord(depositLineService
						.createAndCopy(ds.priceFractCategory, dl)) == 0) {
					throw new ServiceException("errors.system");
				}
			}

			
			setCloseDepositSlipBill(ds, billId, lastCutOffDate, cutoffPdate);

			try{
				this.updateBySqlFile("deposit/UpdateDepositSlip.sql", createParamMap(ds)).execute();
			}catch (Exception e) {
				throw new ServiceException("errors.system");
			}
			updateCount++;
		}
		return updateCount;

	}

	/**
	 * 請求締処理で入金伝票を締解除します.
	 * @param depositSlipList 入金伝票DTOのリスト
	 * @return 更新した件数
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @throws ParseException
	 * @throws SQLException
	 * @throws GeneralSecurityException
	 */
	public int reOpenDepositSlipBill(List<DepositSlipDto> depositSlipList)
			throws ServiceException, UnabledLockException, ParseException,
			SQLException, GeneralSecurityException {

		int updateCount = 0;
		for (DepositSlipDto ds : depositSlipList) {
			
			this.lockRecord(Param.DEPOSIT_SLIP_ID, ds.depositSlipId,
					ds.updDatetm, "deposit/LockSlip.sql");

			
			List<DepositLineDto> dlList = depositLineService.loadBySlip(ds);

			
			for (DepositLineDto dl : dlList) {
				
				dl.status = DepositLine.STATUS_INIT;
				if (depositLineService.updateRecord(depositLineService
						.createAndCopy(ds.priceFractCategory, dl)) == 0) {
					throw new ServiceException("errors.system");
				}
			}

			
			
			ds.status = DepositSlip.STATUS_INIT;
			
			ds.billId = null;
			
			ds.billCutoffDate = null;
			
			ds.billCutoffPdate = null;

			try{
				this.updateBySqlFile("deposit/UpdateDepositSlip.sql", createParamMap(ds)).execute();
			}catch (Exception e) {
				throw new ServiceException("errors.system");
			}
			updateCount++;
		}
		return updateCount;

	}

	/**
	 * 売掛締処理で入金伝票を締めます.
	 * @param depositSlipList 入金伝票DTOのリスト
	 * @param artBalanceId 売掛残一覧ID
	 * @param lastCutOffDate 請求締日の文字列
	 * @param cutoffPdate 請求締処理日
	 * @return 更新した件数
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @throws ParseException
	 * @throws SQLException
	 * @throws GeneralSecurityException
	 */
	public int closeDepositSlipArt(List<DepositSlipDto> depositSlipList,
			Integer artBalanceId, String lastCutOffDate, Timestamp cutoffPdate)
			throws ServiceException, UnabledLockException, ParseException,
			SQLException, GeneralSecurityException {

		int updateCount = 0;
		for (DepositSlipDto ds : depositSlipList) {
			
			this.lockRecord(Param.DEPOSIT_SLIP_ID, ds.depositSlipId,
					ds.updDatetm, "deposit/LockSlip.sql");

			
			if (!isCreditType(ds)) {
				
				List<DepositLineDto> dlList = depositLineService.loadBySlip(ds);

				
				for (DepositLineDto dl : dlList) {
					
					dl.status = DepositLine.STATUS_CLOSE;
					if (depositLineService.updateRecord(depositLineService
							.createAndCopy(ds.priceFractCategory, dl)) == 0) {
						throw new ServiceException("errors.system");
					}
				}
			}


			
			setCloseDepositSlipArt(ds, artBalanceId, lastCutOffDate,
					cutoffPdate);
			try{
				this.updateBySqlFile("deposit/UpdateDepositSlip.sql", createParamMap(ds)).execute();
			}catch (Exception e) {
				throw new ServiceException("errors.system");
			}
			updateCount++;
		}
		return updateCount;

	}

	/**
	 * 売掛締処理で入金伝票を締解除します.
	 * @param depositSlipList 入金伝票DTOのリスト
	 * @return 更新した件数
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @throws ParseException
	 * @throws SQLException
	 * @throws GeneralSecurityException
	 */
	public int reOpenDepositSlipArt(List<DepositSlipDto> depositSlipList)
			throws ServiceException, UnabledLockException, ParseException,
			SQLException, GeneralSecurityException {

		int updateCount = 0;
		for (DepositSlipDto ds : depositSlipList) {
			
			this.lockRecord(Param.DEPOSIT_SLIP_ID, ds.depositSlipId,
					ds.updDatetm, "deposit/LockSlip.sql");

			if (!isCreditType(ds)) {
				
				List<DepositLineDto> dlList = depositLineService.loadBySlip(ds);

				
				for (DepositLineDto dl : dlList) {
					
					dl.status = DepositLine.STATUS_INIT;
					if (depositLineService.updateRecord(depositLineService
							.createAndCopy(ds.priceFractCategory, dl)) == 0) {
						throw new ServiceException("errors.system");
					}
				}
			}

			
			setReOpenDepositSlipArt(ds);
			try{
				this.updateBySqlFile("deposit/UpdateDepositSlip.sql", createParamMap(ds)).execute();
			}catch (Exception e) {
				throw new ServiceException("errors.system");
			}
			updateCount++;
		}
		return updateCount;

	}

	/**
	 * 入金伝票が売掛種別か否かを判断します.
	 * @param ds 入金伝票DTO
	 * @return 売掛種別か否か
	 */
	private boolean isCreditType(DepositSlipDto ds) {
		if (CategoryTrns.DEPOSIT_CATEGORY_CASH_ON_DELIVERY
				.equals(ds.depositCategory)
				|| CategoryTrns.DEPOSIT_CATEGORY_CREDIT_CARD
						.equals(ds.depositCategory)
				|| CategoryTrns.DEPOSIT_CATEGORY_PAY_FIRST
						.equals(ds.depositCategory)
				|| CategoryTrns.DEPOSIT_CATEGORY_CASH
						.equals(ds.depositCategory)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 売上伝票入力情報を使用した入金伝票を作成します.
	 * @param dto 売上伝票DTO
	 * @return 採番した伝票ID
	 * @throws Exception
	 */
	public Long insertBySales(SalesSlipDto dto) throws Exception {
		Long newSlipId = -1L;
		try {
			
			DepositSlipDto tempDto = new DepositSlipDto();
			DepositLineDto tempLineDto = (DepositLineDto) tempDto
					.createLineDto();
			tempDto.status = Constants.STATUS_DEPOSIT_SLIP.PAID;

			
			tempDto.depositDate = dto.salesDate; 
			tempDto.inputPdate = dto.salesDate; 
			tempDto.depositCategory = CategoryTrns.DEPOSIT_CATEGORY_CASH; 
			tempDto.depositAbstract = "";
			tempDto.customerCode = dto.customerCode;
			tempDto.customerName = dto.customerName;
			tempDto.cutoffGroup = this.categoryService
					.cutoffGroupCategoryToCutoffGroup(dto.cutoffGroupCategory);
			tempDto.paybackCycleCategory = dto.paybackCycleCategory;
			tempDto.salesCmCategory = dto.custsalesCmCategory;

			tempDto.baCode = dto.baCode;
			tempDto.baName = dto.baName;
			tempDto.baKana = dto.baKana;
			tempDto.baOfficeName = dto.baOfficeName;
			tempDto.baOfficeKana = dto.baOfficeKana;
			tempDto.baDeptName = dto.baDeptName;
			tempDto.baZipCode = dto.baZipCode;
			tempDto.baAddress1 = dto.baAddress1;
			tempDto.baAddress2 = dto.baAddress2;
			tempDto.baPcName = dto.baPcName;
			tempDto.baPcKana = dto.baPcKana;
			tempDto.baPcPreCatrgory = dto.baPcPreCategory;
			tempDto.baPcPre = dto.baPcPre;
			tempDto.baTel = dto.baTel;
			tempDto.baFax = dto.baFax;
			tempDto.baEmail = dto.baEmail;
			tempDto.baUrl = dto.baUrl;

			tempDto.salesSlipId = dto.salesSlipId; 

			
			tempLineDto.depositSlipId = newSlipId.toString();
			
			tempLineDto.status = Constants.STATUS_DEPOSIT_LINE.PAID;
			Double dTotal = Double.parseDouble(dto.ctaxPriceTotal)
					+ Double.parseDouble(dto.priceTotal);
			tempLineDto.price = dTotal.toString();
			tempLineDto.remarks = MessageResourcesUtil
					.getMessage("labels.deposit.remaks");
			tempDto.getLineDtoList().add(tempLineDto);

			return Long.valueOf(insertRecord(tempDto));

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 売上伝票削除時に入金伝票を削除します.
	 * @param dto 売上伝票DTO
	 * @return 削除した件数
	 * @throws Exception
	 */
	public int deleteBySales(SalesSlipDto dto) throws Exception {
		try {

			
			LinkedHashMap<String, Object> conditions = new LinkedHashMap<String, Object>();

			
			
			conditions.put(Param.SALES_SLIP_ID, dto.salesSlipId);

			List<DepositSlip> dsList = findByCondition(conditions, params,
					"deposit/FindDepositSlipBySalesId.sql");
			if (dsList.size() != 1) {
				return 0;
			}

			DepositSlipDto tempDto = new DepositSlipDto();
			copy(dsList.get(0), tempDto);

			super.updateAudit(tempDto.depositSlipId);
			int count = deleteById(tempDto.depositSlipId, tempDto.updDatetm);

			
			depositLineService.updateAudit(tempDto.depositSlipId);
			depositLineService.deleteRecords(tempDto.depositSlipId);

			return count;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 *
	 * @param id 伝票ID
	 * @return {@link DepositSlipDto}のリスト
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#loadBySlipId(java.lang.String)
	 */
	@Override
	public DepositSlipDto loadBySlipId(String id) throws ServiceException,
			UnabledLockException {
		LinkedHashMap<String, Object> conditions = new LinkedHashMap<String, Object>();

		
		
		conditions.put(Param.DEPOSIT_SLIP_ID, id);

		List<DepositSlip> l = findByCondition(conditions, params,
				"deposit/FindDepositSlip.sql");
		if (l == null || l.size() != 1) {
			return null;
		}

		return this.createAndCopy(l.get(0));
	}

	/**
	 *
	 * @param dto {@link DepositSlipDto}
	 * @param abstractServices 処理内で使用するサービス
	 * @return ロック結果
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#save(java.lang.Object,jp.co.arkinfosys.service.AbstractService[])
	 */
	@Override
	public int save(DepositSlipDto dto, AbstractService<?>... abstractServices)
			throws ServiceException, UnabledLockException {
		this.setSlipData(dto);
		if (dto.depositSlipId == null || dto.depositSlipId.length() == 0) {
			return this.insertRecord(dto);
		}
		return this.updateRecord(dto);
	}

	/**
	 *
	 * @param dto {@link DepositSlipDto}
	 * @return 登録した件数
	 * @throws ServiceException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#insertRecord(java.lang.Object)
	 */
	@Override
	protected int insertRecord(DepositSlipDto dto) throws ServiceException {

		
		Long newSlipId = seqMakerService.nextval(DepositSlip.TABLE_NAME);
		dto.depositSlipId = newSlipId.toString();

		
		Map<String, Object> param = new HashMap<String, Object>();

		
		BeanMap map = Beans.createAndCopy(BeanMap.class,
				this.createAndCopy(dto)).execute();
		param.putAll(map);

		
		Map<String, Object> CommonParam = super.createSqlParam();
		param.putAll(CommonParam);

		
		return this.updateBySqlFile("deposit/InsertDepositSlip.sql", param)
				.execute();
	}

	/**
	 *
	 * @param dto {@link DepositSlipDto}
	 * @return 更新した件数
	 * @throws UnabledLockException
	 * @throws ServiceException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#updateRecord(java.lang.Object)
	 */
	@Override
	protected int updateRecord(DepositSlipDto dto) throws UnabledLockException,
			ServiceException {
		
		this.lockRecord(Param.DEPOSIT_SLIP_ID, dto.depositSlipId,
				dto.updDatetm, "deposit/LockSlip.sql");

		
		Map<String, Object> param = new HashMap<String, Object>();

		
		BeanMap map = Beans.createAndCopy(BeanMap.class,
				this.createAndCopy(dto)).execute();
		param.putAll(map);

		
		Map<String, Object> CommonParam = super.createSqlParam();
		param.putAll(CommonParam);

		
		return this.updateBySqlFile("deposit/UpdateDepositSlip.sql", param)
				.execute();
	}

	/**
	 * 入力内容を入金伝票DTOに設定します.
	 * @param dto 入金伝票DTO
	 * @throws ServiceException
	 */
	private void setSlipData(DepositSlipDto dto) throws ServiceException {

		try {
			
			dto.status = Constants.STATUS_DEPOSIT_SLIP.PAID;

			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat(Constants.FORMAT.DATE)
					.parse(dto.depositDate));
			dto.depositAnnual = String.valueOf(cal.get(Calendar.YEAR));
			dto.depositMonthly = String.valueOf(cal.get(Calendar.MONTH) + 1);
			dto.depositYm = dto.depositAnnual + dto.depositMonthly;

			BigDecimal total = new BigDecimal(0);

			
			for (DepositLineDto lineDto : dto.getLineDtoList()) {
				if (!this.depositLineService.check(lineDto)) {
					continue;
				}
				BigDecimal tmpNum = new BigDecimal(lineDto.price);
				if (tmpNum != null) {
					total = total.add(tmpNum);
				}
			}

			
			dto.depositTotal = total.toString();

			
			List<DeliveryAndPre> deliveryList = this.deliveryService
					.searchDeliveryByCompleteCustomerCode(dto.customerCode);
			dto.baCode = deliveryList.get(0).deliveryCode;
			dto.baKana = deliveryList.get(0).deliveryKana;
			dto.baOfficeName = deliveryList.get(0).deliveryOfficeName;
			dto.baOfficeKana = deliveryList.get(0).deliveryOfficeKana;
			dto.baDeptName = deliveryList.get(0).deliveryDeptName;
			dto.baPcPre = deliveryList.get(0).categoryCodeName;
			dto.baUrl = deliveryList.get(0).deliveryUrl;
		} catch (ParseException e) {
			new ServiceException(e);
		}

	}

	/**
	 *
	 * @param id 伝票ID
	 * @param updDatetm 更新日時の文字列
	 * @return 削除した件数
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#deleteById(java.lang.String, java.lang.String)
	 */
	@Override
	public int deleteById(String id, String updDatetm) throws ServiceException,
			UnabledLockException {

		
		this.lockRecord(Param.DEPOSIT_SLIP_ID, id, updDatetm,
				"deposit/LockSlip.sql");

		
		Map<String, Object> param = new HashMap<String, Object>();

		
		param.put(Param.DEPOSIT_SLIP_ID, id);

		
		Map<String, Object> CommonParam = super.createSqlParam();
		param.putAll(CommonParam);

		
		return this.updateBySqlFile("deposit/DeleteDepositSlip.sql", param)
				.execute();
	}

	/**
	 *
	 * @return DEPOSIT_SLIP_TRN
	 * @see jp.co.arkinfosys.service.AbstractSlipService#getTableName()
	 */
	@Override
	protected String getTableName() {
		return "DEPOSIT_SLIP_TRN";
	}

	/**
	 *
	 * @return DEPOSIT_SLIP_ID
	 * @see jp.co.arkinfosys.service.AbstractSlipService#getKeyColumnName()
	 */
	@Override
	protected String getKeyColumnName() {
		return "DEPOSIT_SLIP_ID";
	}
}
