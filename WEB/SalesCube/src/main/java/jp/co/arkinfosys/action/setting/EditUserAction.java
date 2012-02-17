/*
 *  Copyright 2009-2010 Ark Information Systems.
 */

package jp.co.arkinfosys.action.setting;

import java.util.List;

import javax.annotation.Resource;

import jp.co.arkinfosys.action.CommonResources;
import jp.co.arkinfosys.common.Constants;
import jp.co.arkinfosys.common.StringUtil;
import jp.co.arkinfosys.dto.UserDto;
import jp.co.arkinfosys.dto.setting.MenuDto;
import jp.co.arkinfosys.entity.Dept;
import jp.co.arkinfosys.entity.User;
import jp.co.arkinfosys.entity.join.MenuJoin;
import jp.co.arkinfosys.form.setting.EditUserForm;
import jp.co.arkinfosys.service.DeptService;
import jp.co.arkinfosys.service.MenuService;
import jp.co.arkinfosys.service.UserService;
import jp.co.arkinfosys.service.exception.ServiceException;
import jp.co.arkinfosys.service.exception.UnabledLockException;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.LabelValueBean;
import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ActionMessagesUtil;

/**
 * 社員情報編集画面のアクションクラスです.
 * @author Ark Information Systems
 *
 */
public class EditUserAction extends CommonResources {

	/**
	 * 画面遷移用のマッピングクラスです.
	 * @author Ark Information Systems
	 *
	 */
	public static class Mapping {
		public static final String INPUT = "editUser.jsp";
	}

	@ActionForm
	@Resource
	private EditUserForm editUserForm;

	@Resource
	private UserService userService;

	@Resource
	private DeptService deptService;

	@Resource
	private MenuService menuService;

	/**
	 * 新規登録時の初期化処理を行います.<br>
	 * 処理実行後、{@link Mapping#INPUT}で定義されたURIに遷移します.
	 * @return 画面遷移先のURI文字列
	 * @throws Exception
	 */
	@Execute(validator = false)
	public String index() throws Exception {
		try {
			this.init(null);
		} catch (ServiceException e) {
			super.errorLog(e);
			throw new ServiceException(e);
		}

		return EditUserAction.Mapping.INPUT;
	}

	/**
	 * 編集モード時の初期化処理を行います.<br>
	 * 処理実行後、{@link Mapping#INPUT}で定義されたURIに遷移します.
	 * @return 画面遷移先のURI文字列
	 * @throws Exception
	 */
	@Execute(validator = false, urlPattern = "edit/{userId}")
	public String edit() throws Exception {
		try {
			this.editUserForm.userId = StringUtil
					.decodeSL(this.editUserForm.userId);
			this.init(this.editUserForm.userId);
		} catch (ServiceException e) {
			super.errorLog(e);
			throw e;
		}
		return EditUserAction.Mapping.INPUT;
	}

	/**
	 * 登録処理を行います.<br>
	 * 登録完了時および何かしらの問題があった場合に、画面にメッセージを表示します.<br>
	 * 処理実行後、{@link Mapping#INPUT}で定義されたURIに遷移します.
	 * @return 画面遷移先のURI文字列
	 * @throws Exception
	 */
	@Execute(validator = true, validate = "validateForInsert", input = EditUserAction.Mapping.INPUT)
	public String insert() throws Exception {
		try {
			User user = this.userService.findById(this.editUserForm.userId);
			if (user != null) {
				super.messages.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("errors.user.already.exists"));
				ActionMessagesUtil.addErrors(super.httpRequest, super.messages);
				return EditUserAction.Mapping.INPUT;
			}

			UserDto dto = Beans.createAndCopy(UserDto.class, this.editUserForm)
					.execute();

			
			this.userService.insertRecord(dto);

			this.init(this.editUserForm.userId);

			super.messages.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("infos.insert"));
			ActionMessagesUtil.addMessages(super.httpRequest, super.messages);
		} catch (ServiceException e) {
			super.errorLog(e);
			throw e;
		}
		return EditUserAction.Mapping.INPUT;
	}

	/**
	 * 更新処理を行います.<br>
	 * 更新完了時に、画面にメッセージを表示します.<br>
	 * 処理実行後、{@link Mapping#INPUT}で定義されたURIに遷移します.
	 * @return 画面遷移先のURI文字列
	 * @throws Exception
	 */
	@Execute(validator = true, validate = "validateForUpdate", input = EditUserAction.Mapping.INPUT)
	public String update() throws Exception {
		try {
			UserDto dto = Beans.createAndCopy(UserDto.class, this.editUserForm)
					.execute();

			
			this.userService.updateRecord(dto);

			this.init(this.editUserForm.userId);

			super.messages.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("infos.update"));
			ActionMessagesUtil.addMessages(super.httpRequest, super.messages);
		} catch (UnabledLockException e) {
			super.errorLog(e);

			super.messages.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage(e.getKey()));
			ActionMessagesUtil.addErrors(super.httpRequest, super.messages);
		} catch (ServiceException e) {
			super.errorLog(e);
			throw e;
		}
		return EditUserAction.Mapping.INPUT;
	}

	/**
	 * 削除処理を行います.<br>
	 * 削除完了時に、画面にメッセージを表示します.<br>
	 * 処理実行後、{@link Mapping#INPUT}で定義されたURIに遷移します.
	 * @return 画面遷移先のURI文字列
	 * @throws Exception
	 */
	@Execute(validator = false)
	public String delete() throws Exception {
		try {
			UserDto dto = Beans.createAndCopy(UserDto.class, this.editUserForm)
					.execute();

			
			this.userService.deleteRecord(dto);

			this.init(null);

			super.messages.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("infos.delete"));
			ActionMessagesUtil.addMessages(super.httpRequest, super.messages);
		} catch (UnabledLockException e) {
			super.errorLog(e);

			super.messages.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage(e.getKey()));
			ActionMessagesUtil.addErrors(super.httpRequest, super.messages);
		} catch (ServiceException e) {
			super.errorLog(e);
			throw e;
		}
		return EditUserAction.Mapping.INPUT;
	}

	/**
	 * リセット処理を行います.<br>
	 * 処理実行後、{@link Mapping#INPUT}で定義されたURIに遷移します.
	 * @return 画面遷移先のURI文字列
	 * @throws Exception
	 */
	@Execute(validator = false)
	public String reset() throws Exception {
		if (this.editUserForm.editMode) {
			this.init(this.editUserForm.userId);
		} else {
			this.init(null);
		}
		return EditUserAction.Mapping.INPUT;
	}

	/**
	 * アクションフォームを初期化します.
	 * @param userId 社員コード
	 * @throws ServiceException
	 */
	private void init(String userId) throws ServiceException {
		this.editUserForm.reset();

		this.editUserForm.isUpdate = super.userDto
				.isMenuUpdate(Constants.MENU_ID.SETTING_USER);

		List<Dept> deptList = this.deptService.findAllDept();
		for (Dept dept : deptList) {
			LabelValueBean bean = new LabelValueBean();
			bean.setLabel(dept.name);
			bean.setValue(dept.deptId);
			this.editUserForm.deptList.add(bean);
		}
		this.editUserForm.deptList.add(0, new LabelValueBean());

		
		List<MenuJoin> menuJoinList = this.menuService
				.findAllMenuValidFlagForUser(userId);
		for (MenuJoin menuJoin : menuJoinList) {
			this.editUserForm.menuDtoList.add(Beans.createAndCopy(
					MenuDto.class, menuJoin).timestampConverter(
					Constants.FORMAT.TIMESTAMP).dateConverter(
					Constants.FORMAT.DATE).execute());
		}

		this.editUserForm.menuCount = menuJoinList.size();

		if (!StringUtil.hasLength(userId)) {
			
			for (MenuDto menuDto : this.editUserForm.menuDtoList) {
				if (Constants.MENU_VALID_TYPE.INVALID_VALID_REFERENCE
						.equals(menuDto.validType)) {
					menuDto.validFlag = Constants.MENU_VALID_LEVEL.VALID_LIMITATION;
				} else {
					menuDto.validFlag = Constants.MENU_VALID_LEVEL.INVALID;
				}
			}
			return;
		}

		
		User user = this.userService.findById(userId);
		if (user == null) {
			return;
		}
		Beans.copy(user, this.editUserForm).timestampConverter(
				Constants.FORMAT.TIMESTAMP)
				.dateConverter(Constants.FORMAT.DATE).execute();

		
		this.editUserForm.creDatetmShow = StringUtil.getDateString(
				Constants.FORMAT.DATE, user.creDatetm);
		this.editUserForm.updDatetmShow = StringUtil.getDateString(
				Constants.FORMAT.DATE, user.updDatetm);

		this.editUserForm.editMode = true;
	}
}
