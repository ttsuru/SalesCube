/*
 *  Copyright 2009-2010 Ark Information Systems.
 */

package jp.co.arkinfosys.action.ajax.dialog;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import jp.co.arkinfosys.common.CategoryTrns;
import jp.co.arkinfosys.common.Constants;
import jp.co.arkinfosys.common.NumberUtil;
import jp.co.arkinfosys.dto.setting.FileInfoDto;
import jp.co.arkinfosys.entity.join.FileInfoJoin;
import jp.co.arkinfosys.service.AbstractService;
import jp.co.arkinfosys.service.FileInfoService;
import jp.co.arkinfosys.service.exception.ServiceException;

import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.annotation.Required;

/**
 * ファイル参照ダイアログの表示処理アクションクラスです.
 *
 * @author Ark Information Systems
 *
 */
public class ReferFilesDialogAction extends AbstractDialogAction {

	/**
	 * ファイル情報テーブルに対するサービスクラスです.
	 */
	@Resource
	private FileInfoService fileInfoService;

	/**
	 * ダイアログIDです.
	 */
	@Required
	public String dialogId;

	/**
	 * ファイル情報件数です.
	 */
	public int fileInfoCount = 0;

	/**
	 * ファイル情報DTOのリストです.
	 */
	public List<FileInfoDto> fileInfoDtoList = new ArrayList<FileInfoDto>();

	/**
	 * ファイル情報テーブルからユーザーが閲覧可能なファイル情報を取得します.
	 *
	 * @throws ServiceException サービス例外発生時
	 */
	@Override
	protected void createList() throws ServiceException {
		
		HashMap<String, Object> conditions = new HashMap<String, Object>();
		conditions.put(FileInfoService.Param.OPEN_LEVEL,
				super.userDto.fileOpenLevel);

		List<FileInfoJoin> fileInfoList = this.fileInfoService.findByCondition(
				conditions, AbstractService.Param.CRE_DATETM, false);

		
		this.fileInfoCount = fileInfoList.size();

		for (FileInfoJoin fileInfo : fileInfoList) {
			FileInfoDto fileInfoDto = Beans.createAndCopy(FileInfoDto.class,
					fileInfo).timestampConverter(Constants.FORMAT.DATE)
					.dateConverter(Constants.FORMAT.DATE).excludes("fileSize")
					.execute();
			fileInfoDtoList.add(fileInfoDto);

			
			if (fileInfo.fileSize == null) {
				continue;
			}
			fileInfoDto.fileSize = this.byteToKBStr(fileInfo.fileSize
					.intValue());
		}
	}

	/**
	 * バイト数をキロバイト単位の文字列表現に変換します.
	 *
	 * @param value ファイルのバイト数
	 * @return キロバイト数に変換した結果文字列
	 */
	private String byteToKBStr(long value) {
		BigDecimal valueDec = new BigDecimal(value);
		BigDecimal baseDec = new BigDecimal(Constants.FILE_SIZE.BLOCK_SIZE);
		valueDec = valueDec.divide(baseDec, MathContext.UNLIMITED);

		
		DecimalFormat kbFormat = NumberUtil.createDecimalFormat(
				CategoryTrns.FLACT_CATEGORY_UP, 0, true);
		return kbFormat.format(valueDec.doubleValue()) + Constants.FILE_SIZE.KB;
	}

}
