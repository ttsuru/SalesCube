/*
 *  Copyright 2009-2010 Ark Information Systems.
 */

package jp.co.arkinfosys.service.payment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.co.arkinfosys.common.CategoryTrns;
import jp.co.arkinfosys.common.Constants;
import jp.co.arkinfosys.common.StringUtil;
import jp.co.arkinfosys.dto.YmDto;
import jp.co.arkinfosys.dto.payment.InputPaymentDto;
import jp.co.arkinfosys.dto.payment.InputPaymentLineDto;
import jp.co.arkinfosys.entity.PaymentSlipTrn;
import jp.co.arkinfosys.entity.Rate;
import jp.co.arkinfosys.entity.join.PoSlipJoin;
import jp.co.arkinfosys.entity.join.PoSlipLineJoin;
import jp.co.arkinfosys.entity.join.SupplierJoin;
import jp.co.arkinfosys.s2extend.NumberConverter;
import jp.co.arkinfosys.service.AbstractService;
import jp.co.arkinfosys.service.AbstractSlipService;
import jp.co.arkinfosys.service.RateService;
import jp.co.arkinfosys.service.SeqMakerService;
import jp.co.arkinfosys.service.SupplierService;
import jp.co.arkinfosys.service.YmService;
import jp.co.arkinfosys.service.exception.ServiceException;
import jp.co.arkinfosys.service.exception.UnabledLockException;

import org.seasar.extension.jdbc.exception.SNonUniqueResultException;
import org.seasar.framework.beans.Converter;
import org.seasar.framework.beans.util.Beans;

/**
 * 支払入力サービスクラスです.
 *
 * @author Ark Information Systems
 */
public class InputPaymentService extends AbstractSlipService<PaymentSlipTrn, InputPaymentDto> {

	@Resource
	private SeqMakerService seqMakerService;

	@Resource
	private YmService ymService;

	@Resource
	private SupplierService supplierService;

	@Resource
	private RateService rateService;

	/**
	 *
	 * パラメータ定義クラスです.
	 *
	 */
	public static class Param {
		public static final String PAYMENT_SLIP_ID = "paymentSlipId";
		public static final String PAYMENT_LINE_ID = "paymentLineId";
		public static final String PO_SLIP_ID = "poSlipId";
		public static final String UPD_DATE_TM = "updDatetm";
		public static final String SUPPLIER_SLIP_ID = "supplierSlipId";
		public static final String STATUS = "status";
		public static final String PAYMENT_DATE = "paymentDate";
		public static final String USER_ID = "userId";
		public static final String USER_NAME = "userName";
		public static final String SUPPLIER_CODE = "supplierCode";
		public static final String SUPPLIER_NAME = "supplierName";
		public static final String RATE_ID = "rateId";
		public static final String PRICE_TOTAL = "priceTotal";
		public static final String FE_PRICE_TOTAL = "fePriceTotal";
		public static final String REMARKS = "remarks";
		public static final String PAYMENT_ANNUAL = "paymentAnnual";
		public static final String PAYMENT_MONTHLY = "paymentMonthly";
		public static final String PAYMENT_YM = "paymentYm";
		public static final String TAX_SHIFT_CATEGORY = "taxShiftCategory";
		public static final String TAX_FRACT_CATEGORY = "taxFractCategory";
		public static final String PRICE_FRACT_CATEGORY = "priceFractCategory";
		public static final String APT_BALANCE_ID = "aptBalanceId";

		public static final String SUPPLIER_LINE_ID = "supplierLineId";
	}

	/**
	 * 支払伝票を登録します.
	 * @param dto 支払伝票DTO
	 * @return 登録件数
	 * @throws ServiceException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#insertRecord(java.lang.Object)
	 */
	@Override
	protected int insertRecord(InputPaymentDto dto) throws ServiceException {
		try {
			PaymentSlipTrn entity = Beans.createAndCopy(PaymentSlipTrn.class, dto).execute();
			Map<String, Object> param = setEntityToParam(entity);

			return this.updateBySqlFile("payment/InsertPaymentSlipTrn.sql", param).execute();

		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 支払伝票を更新します.
	 *
	 * @param dto 支払伝票DTO
	 * @return レコードロック結果
	 * @throws UnabledLockException
	 * @throws ServiceException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#updateRecord(java.lang.Object)
	 */
	@Override
	protected int updateRecord(InputPaymentDto dto) throws UnabledLockException, ServiceException {
		PaymentSlipTrn entity = Beans.createAndCopy(PaymentSlipTrn.class, dto).execute();

		
		int lockResult = this.lockPaymentRecord(dto);

		Map<String, Object> param = setEntityToParam(entity);
		this.updateBySqlFile("payment/UpdatePaymentSlipTrn.sql", param).execute();

		return lockResult;
	}

	/**
	 * 支払伝票を削除します.<br>
	 * 引数の伝票番号に該当する支払伝票関連情報を削除します.
	 *
	 * @param id 伝票番号
	 * @param updDatetm 更新日時
	 * @return 削除件数
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#deleteById(java.lang.String,java.lang.String)
	 */
	@Override
	public int deleteById(String id, String updDatetm) throws ServiceException, UnabledLockException {
		Map<String, Object> param = super.createSqlParam();
		param.put(InputPaymentService.Param.PAYMENT_SLIP_ID, id);

		return this.updateBySqlFile("payment/DeletePaymentSlipTrnBySlipId.sql", param).execute();
	}

	/**
	 * 支払伝票を取得します.<br>
	 * 引数の伝票番号をキーに支払伝票情報を取得します.
	 *
	 * @param id 伝票番号
	 * @return 支払伝票DTO
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#loadBySlipId(java.lang.String)
	 */
	@Override
	public InputPaymentDto loadBySlipId(String id) throws ServiceException, UnabledLockException {
		try {
			Map<String, Object> param = super.createSqlParam();
			param.put(InputPaymentService.Param.PAYMENT_SLIP_ID, id);

			PaymentSlipTrn entity = this.selectBySqlFile(PaymentSlipTrn.class, "payment/FindPaymentSlipTrnBySlipId.sql", param).getSingleResult();

			if(entity != null) {
				InputPaymentDto dto = new InputPaymentDto();
				Beans.copy(entity, dto).dateConverter(Constants.FORMAT.TIMESTAMP, "updDatetm").execute();

				
				if(StringUtil.hasLength(dto.rateId)){
					Rate rate = rateService.findById(dto.rateId);
					dto.cUnitSign = rate.sign;
				}
				return dto;
			}

			return null;

		} catch(SNonUniqueResultException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 支払伝票の新規登録・更新処理を行います.
	 *
	 * @param dto 支払伝票DTO
	 * @param abstractServices サービスリスト
	 * @return ロック結果
	 * @throws ServiceException
	 * @throws UnabledLockException
	 * @see jp.co.arkinfosys.service.AbstractSlipService#save(java.lang.Object,
	 *      jp.co.arkinfosys.service.AbstractService[])
	 */
	@Override
	public int save(InputPaymentDto dto, AbstractService<?>... abstractServices) throws ServiceException, UnabledLockException {
		
		YmDto ymDto = ymService.getYm(dto.paymentDate);
		if(ymDto == null) {
			dto.paymentAnnual = "";
			dto.paymentMonthly = "";
			dto.paymentYm = "";
		} else {
			dto.paymentAnnual = ymDto.annual.toString();
			dto.paymentMonthly = ymDto.monthly.toString();
			dto.paymentYm = ymDto.ym.toString();
		}

		
		if("".equals(dto.fePriceTotal)) {
			dto.fePriceTotal = "0";
		}

		int lockResult = LockResult.SUCCEEDED;

		if(dto.paymentSlipId == null || dto.paymentSlipId.length() == 0) {
			
			dto.paymentSlipId = Long.toString(seqMakerService.nextval(PaymentSlipTrn.TABLE_NAME));

			
			insertRecord(dto);

		} else {
			
			lockResult = updateRecord(dto);
		}

		return lockResult;
	}

	/**
	 * 検索条件パラメータを設定して返します.
	 * @param entity 支払伝票エンティティ
	 * @return 検索条件パラメータ
	 */
	private Map<String, Object> setEntityToParam(PaymentSlipTrn entity) {
		Map<String, Object> param = super.createSqlParam();

		
		param.put(InputPaymentService.Param.PAYMENT_SLIP_ID, entity.paymentSlipId);

		
		param.put(InputPaymentService.Param.STATUS, entity.status);

		
		param.put(InputPaymentService.Param.PAYMENT_DATE, entity.paymentDate);

		
		param.put(InputPaymentService.Param.USER_ID, entity.userId);

		
		param.put(InputPaymentService.Param.USER_NAME, entity.userName);

		
		param.put(InputPaymentService.Param.SUPPLIER_CODE, entity.supplierCode);

		
		param.put(InputPaymentService.Param.SUPPLIER_NAME, entity.supplierName);

		
		param.put(InputPaymentService.Param.RATE_ID, entity.rateId);

		
		param.put(InputPaymentService.Param.PRICE_TOTAL, entity.priceTotal);

		
		param.put(InputPaymentService.Param.FE_PRICE_TOTAL, entity.fePriceTotal);

		
		param.put(InputPaymentService.Param.PO_SLIP_ID, entity.poSlipId);

		
		param.put(InputPaymentService.Param.REMARKS, entity.remarks);

		
		param.put(InputPaymentService.Param.PAYMENT_ANNUAL, entity.paymentAnnual);

		
		param.put(InputPaymentService.Param.PAYMENT_MONTHLY, entity.paymentMonthly);

		
		param.put(InputPaymentService.Param.PAYMENT_YM, entity.paymentYm);

		
		param.put(InputPaymentService.Param.TAX_SHIFT_CATEGORY, entity.taxShiftCategory);

		
		param.put(InputPaymentService.Param.TAX_FRACT_CATEGORY, entity.taxFractCategory);

		
		param.put(InputPaymentService.Param.PRICE_FRACT_CATEGORY, entity.priceFractCategory);

		
		param.put(InputPaymentService.Param.APT_BALANCE_ID, null);

		return param;
	}

	/**
	 * 支払伝票レコードをロックします.
	 * @param dto 支払伝票DTO
	 * @return ロック結果
	 * @throws ServiceException
	 * @throws UnabledLockException
	 */
	public int lockPaymentRecord(InputPaymentDto dto) throws ServiceException, UnabledLockException {
		return this.lockRecord(InputPaymentService.Param.PAYMENT_SLIP_ID, dto.paymentSlipId, dto.updDatetm, "payment/LockPaymentSlipTrn.sql");
	}

	/**
	 * 仕入伝票レコードをロックします.
	 * @param dto 支払伝票DTO
	 * @return ロック結果
	 * @throws ServiceException
	 * @throws UnabledLockException
	 */
	public int lockSupplierRecord(InputPaymentDto dto) throws ServiceException, UnabledLockException {
		
		Map<String, String> supplierMap = dto.getSupplierSlipIdAndUpdateTime();
		Iterator<String> iterator = supplierMap.keySet().iterator();

		
		while(iterator.hasNext()) {
			String id = iterator.next();
			String updDateTm = supplierMap.get(id);
			this.lockRecord(InputPaymentService.Param.SUPPLIER_SLIP_ID, id, updDateTm, "payment/LockSupplierSlipTrn.sql");
		}

		return LockResult.SUCCEEDED;
	}

	/**
	 * 仕入伝票を更新します.<br>
	 * 支払伝票更新時に呼び出され、仕入伝票を更新します.
	 * @param dto 支払伝票DTO
	 * @throws ServiceException
	 * @throws UnabledLockException
	 */
	public void updateSupplierForUpdate(InputPaymentDto dto) throws ServiceException, UnabledLockException {
		
		this.lockSupplierRecord(dto);

		
		HashSet<String> supIdList = new HashSet<String>();
		for(InputPaymentLineDto line : dto.getLineDtoList()) {
			
			if(line.checkPayLine) {
				supIdList.add(line.supplierSlipId);
			}
		}

		
		Map<String, Object> param = super.createSqlParam();

		
		for(String supId : supIdList) {

			
			if(isAllPaidSupplierSlip(dto, supId)) {
				param.put(InputPaymentService.Param.STATUS, Constants.STATUS_SUPPLIER_SLIP.PAID);
			} else {
				param.put(InputPaymentService.Param.STATUS, Constants.STATUS_SUPPLIER_SLIP.PAYING);
			}

			param.put(InputPaymentService.Param.SUPPLIER_SLIP_ID, supId);
			param.put(InputPaymentService.Param.PAYMENT_SLIP_ID, dto.paymentSlipId);
			param.put(InputPaymentService.Param.PAYMENT_DATE, dto.paymentDate);

			
			this.updateBySqlFile("payment/UpdateSupplierSlipTrnBySupplierSlipId.sql", param).execute();
		}

		
		for(InputPaymentLineDto line : dto.getLineDtoList()) {
			
			if(!line.checkPayLine) {
				continue;
			}

			
			param.put(InputPaymentService.Param.STATUS, Constants.STATUS_SUPPLIER_LINE.PAID);
			param.put(InputPaymentService.Param.SUPPLIER_LINE_ID, line.supplierLineId);
			param.put(InputPaymentService.Param.PAYMENT_LINE_ID, line.paymentLineId); 

			
			this.updateBySqlFile("payment/UpdateSupplierLineTrnBySupplierLineId.sql", param).execute();
		}
	}

	/**
	 * 仕入伝票を更新します.<br>
	 * 支払伝票削除時に呼び出され、仕入伝票を更新します.
	 * @param dto 支払伝票DTO
	 * @throws ServiceException
	 * @throws UnabledLockException
	 */
	public void updateSupplierForDelete(InputPaymentDto dto) throws ServiceException, UnabledLockException {
		
		HashSet<String> supIdList = new HashSet<String>();
		for(InputPaymentLineDto line : dto.getLineDtoList()) {
			supIdList.add(line.supplierSlipId);
		}

		
		Map<String, Object> param = super.createSqlParam();

		for(String supId : supIdList) {
			
			param.put(InputPaymentService.Param.SUPPLIER_SLIP_ID, supId);
			param.put(InputPaymentService.Param.PAYMENT_SLIP_ID, null);
			param.put(InputPaymentService.Param.PAYMENT_DATE, null);

			
			if(countPaymentSlipBySupplierSlipId(supId) > 0) {
				param.put(InputPaymentService.Param.STATUS, Constants.STATUS_SUPPLIER_SLIP.PAYING);
			} else {
				param.put(InputPaymentService.Param.STATUS, Constants.STATUS_SUPPLIER_SLIP.UNPAID);
			}
			
			this.updateBySqlFile("payment/UpdateSupplierSlipTrnBySupplierSlipId.sql", param).execute();
		}

		
		for(InputPaymentLineDto lineDto : dto.getLineDtoList()) {
			
			param.put(InputPaymentService.Param.SUPPLIER_LINE_ID, lineDto.supplierLineId);
			param.put(InputPaymentService.Param.PAYMENT_LINE_ID, null);
			param.put(InputPaymentService.Param.STATUS, Constants.STATUS_SUPPLIER_LINE.UNPAID);

			
			this.updateBySqlFile("payment/UpdateSupplierLineTrnBySupplierLineId.sql", param).execute();
		}
	}

	/**
	 * 仕入伝票に紐づく支払伝票の件数を取得します.
	 * @param supplierSlipId 仕入伝票番号
	 * @return 仕入伝票に紐づく支払伝票の件数
	 */
	public int countPaymentSlipBySupplierSlipId(String supplierSlipId) {
		Map<String, Object> param = super.createSqlParam();
		param.put(InputPaymentService.Param.SUPPLIER_SLIP_ID, supplierSlipId);

		return this.selectBySqlFile(Integer.class, "payment/CountPaymentLineBySupplierSlipId.sql", param).getSingleResult();
	}

	/**
	 * 仕入伝票明細行の全選択チェックです.<br>
	 * 支払伝票の全明細行が引数の仕入伝票番号と紐づいていることをチェックします.
	 * @param dto 支払伝票DTO
	 * @param supId 仕入伝票番号
	 * @return
	 */
	private boolean isAllPaidSupplierSlip(InputPaymentDto dto, String supId) {
		for(InputPaymentLineDto line : dto.getLineDtoList()) {
			if(line.supplierSlipId != null && line.supplierSlipId.equals(supId) && (!line.checkPayLine)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 発注伝票に紐づく未払いの仕入伝票件数を返します.
	 * @param poSlipId 発注伝票番号
	 * @return 未払い仕入伝票件数
	 * @throws ServiceException
	 */
	public int searchUnpaidSupplierSlipCount(String poSlipId) throws ServiceException {
		
		Map<String, Object> param = super.createSqlParam();
		param.put(InputPaymentService.Param.PO_SLIP_ID, poSlipId);
		param.put(InputPaymentService.Param.STATUS, Constants.STATUS_SUPPLIER_SLIP.UNPAID);

		
		return this.selectBySqlFile(Integer.class, "payment/searchUnpaidSupplierSlipCount.sql", param).getSingleResult();
	}

	/**
	 * 発注伝票番号から支払伝票情報を作成します.
	 *
	 * @param poSlipId 発注伝票番号
	 * @return 支払伝票DTO
	 * @throws ServiceException
	 */
	public InputPaymentDto findByPoSlipId(String poSlipId) throws ServiceException {
		
		PoSlipJoin po = getPoSlipTrn(poSlipId);

		
		if(po == null) {
			return null;
		}

		
		SupplierJoin supplier = this.supplierService.findById(po.supplierCode);
		String paymentCategory = this.convertPaymentTypeCategory(supplier.paymentTypeCategory);

		
		List<PoSlipLineJoin> poList = getPoSlipLine(poSlipId);

		
		InputPaymentDto payTrnDto = Beans.createAndCopy(InputPaymentDto.class, po).dateConverter(Constants.FORMAT.TIMESTAMP, "updDatetm").execute();

		
		Converter numConv = new NumberConverter(super.mineDto.productFractCategory, super.mineDto.numDecAlignment, true);

		
		Converter yenConv = new NumberConverter(payTrnDto.priceFractCategory, 0, true);

		
		Converter dolConv = new NumberConverter(payTrnDto.priceFractCategory, super.mineDto.unitPriceDecAlignment, true);

		List<InputPaymentLineDto> lineList = new ArrayList<InputPaymentLineDto>();
		for(PoSlipLineJoin slipLine : poList) {
			InputPaymentLineDto lineDto = Beans.createAndCopy(InputPaymentLineDto.class, slipLine).dateConverter(Constants.FORMAT.TIMESTAMP, "supUpdDatetm").converter(yenConv, "unitPrice", "price").converter(dolConv, "dolUnitPrice", "dolPrice").converter(numConv, "quantity").execute();
			lineDto.paymentCategory = paymentCategory;
			lineList.add(lineDto);
		}
		payTrnDto.setLineDtoList(lineList);

		
		payTrnDto.supplierTaxRate = payTrnDto.getLineDtoList().get(0).ctaxRate;

		
		payTrnDto.cUnitSign = supplier.cUnitSign;

		return payTrnDto;
	}

	/**
	 * 仕入先マスタが持つ「支払方法」の値を、支払伝票明細行が持つ「支払明細区分」の値に変換します.
	 * @param paymentTypeCategory 支払方法
	 * @return 支払明細区分
	 */
	private String convertPaymentTypeCategory(String paymentTypeCategory) {
		if(CategoryTrns.PAYMENT_TYPE_NOTE.equals(paymentTypeCategory)) {
			return CategoryTrns.PAYMENT_DETAIL_NOTE;
		} else if(CategoryTrns.PAYMENT_TYPE_OTHER.equals(paymentTypeCategory)) {
			return CategoryTrns.PAYMENT_DETAIL_OTHER;
		} else {
			return paymentTypeCategory;
		}
	}

	/**
	 * 支払伝票画面表示に必要な情報を返します.<br>
	 * 発注伝票から支払画面表示に必要な情報を取得します.
	 * @param poSlipId 発注伝票番号
	 * @return 支払伝票表示情報
	 * @throws ServiceException
	 */
	protected PoSlipJoin getPoSlipTrn(String poSlipId) throws ServiceException {
		
		Map<String, Object> param = super.createSqlParam();
		param.put(InputPaymentService.Param.PO_SLIP_ID, poSlipId);

		return this.selectBySqlFile(PoSlipJoin.class, "payment/FindPoSlipTrnByPoSlipId.sql", param).getSingleResult();
	}

	/**
	 * 支払伝票画面明細行表示に必要な情報を返します.<br>
	 * 発注伝票・仕入伝票から支払画面明細行表示に必要な情報を取得します.
	 *
	 * @param poSlipId 発注伝票番号
	 * @return 支払伝票明細行表示情報リスト
	 * @throws ServiceException
	 */
	protected List<PoSlipLineJoin> getPoSlipLine(String poSlipId) throws ServiceException {
		
		Map<String, Object> param = super.createSqlParam();
		param.put(InputPaymentService.Param.PO_SLIP_ID, poSlipId);
		param.put(InputPaymentService.Param.STATUS, Constants.STATUS_SUPPLIER_LINE.UNPAID);

		
		return this.selectBySqlFile(PoSlipLineJoin.class, "payment/FindPoSlipLineByPoSlipId.sql", param).getResultList();
	}

	/**
	 * テーブル名を返します.
	 * @return 支払伝票テーブル名
	 */
	@Override
	protected String getTableName() {
		return "PAYMENT_SLIP_TRN";
	}

	/**
	 * キーカラム名を返します.
	 * @return 支払伝票テーブルのキーカラム名
	 */
	@Override
	protected String getKeyColumnName() {
		return "PAYMENT_SLIP_ID";
	}
}
