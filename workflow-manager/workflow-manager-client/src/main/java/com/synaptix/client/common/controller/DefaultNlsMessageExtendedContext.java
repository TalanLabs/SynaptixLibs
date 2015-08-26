package com.synaptix.client.common.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.IdRaw;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.swing.utils.SyDesktop;
import com.synaptix.taskmanager.controller.helper.IExportNlsMessageService;
import com.synaptix.taskmanager.controller.helper.IImportNlsMessageService;
import com.synaptix.taskmanager.controller.helper.INlsMessageData;
import com.synaptix.taskmanager.controller.helper.INlsMessageExtendedContext;
import com.synaptix.taskmanager.controller.helper.INlsMessageService;
import com.synaptix.taskmanager.controller.helper.NlsMessageDataFields;
import com.synaptix.taskmanager.model.XlsSheet;
import com.synaptix.taskmanager.view.ICommonViewFactory;
import com.synaptix.taskmanager.view.swing.ChooseFileTranslationDialogResult;
import com.synaptix.taskmanager.view.swing.ChooseParamImportExcelResult;
import com.synaptix.widget.core.controller.IController;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;
import com.synaptix.widget.viewworker.view.AbstractSavingViewWorker;


public class DefaultNlsMessageExtendedContext implements INlsMessageExtendedContext {

	private static final String[] COLUMN_HEADER = ComponentHelper.PropertyArrayBuilder.build(NlsMessageDataFields.columnName(), NlsMessageDataFields.idObject(), NlsMessageDataFields.defaultMeaning(),
			NlsMessageDataFields.myMeaning(), NlsMessageDataFields.meaning());

	private final ICommonViewFactory viewFactory;

	private final Class<? extends IComponent> componentClass;

	private final IController controller;

	private final Class<? extends IExportNlsMessageService> exportNlsMessageServiceClass;

	private final Class<? extends IImportNlsMessageService> importNlsMessageServiceClass;

	public DefaultNlsMessageExtendedContext(ICommonViewFactory viewFactory, IController controller, Class<? extends IComponent> componentClass) {
		this(viewFactory, controller, componentClass, null, null);
	}

	public DefaultNlsMessageExtendedContext(ICommonViewFactory viewFactory, IController controller, Class<? extends IExportNlsMessageService> exportNlsMessageServiceClass,
			Class<? extends IImportNlsMessageService> importNlsMessageServiceClass) {
		this(viewFactory, controller, null, exportNlsMessageServiceClass, importNlsMessageServiceClass);
	}

	public DefaultNlsMessageExtendedContext(ICommonViewFactory viewFactory, IController controller, Class<? extends IComponent> componentClass,
			Class<? extends IExportNlsMessageService> exportNlsMessageServiceClass, Class<? extends IImportNlsMessageService> importNlsMessageServiceClass) {
		super();

		this.controller = controller;
		this.viewFactory = viewFactory;
		this.componentClass = componentClass;
		this.exportNlsMessageServiceClass = exportNlsMessageServiceClass;
		this.importNlsMessageServiceClass = importNlsMessageServiceClass;
	}

	private final IServiceFactory getPscNormalServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal");
	}

	private final INlsMessageService getNlsMessageService() {
		return getPscNormalServiceFactory().getService(INlsMessageService.class);
	}

	private final IExportNlsMessageService getExportNlsMessageService() {
		return getPscNormalServiceFactory().getService(exportNlsMessageServiceClass);
	}

	private final IImportNlsMessageService getImportNlsMessageService() {
		return getPscNormalServiceFactory().getService(importNlsMessageServiceClass);
	}

	@Override
	public boolean hasAuthChangeNlsMeanings() {
		return StaticCommonHelper.getCommonAuthsBundle().hasWriteNlsMessages();
	}

	private List<INlsMessageData> getNlsMessageDatas(Locale locale) {
		if (exportNlsMessageServiceClass != null) {
			return getExportNlsMessageService().exportNlsMessages(locale);
		} else {
			return getNlsMessageService().exportNlsMessages(componentClass, locale);
		}
	}

	private int setNlsMessageDatas(Locale locale, List<INlsMessageData> nlsMessageDatas) {
		if (importNlsMessageServiceClass != null) {
			return getImportNlsMessageService().importNlsMessages(locale, nlsMessageDatas);
		} else {
			return getNlsMessageService().importNlsMessages(componentClass, locale, nlsMessageDatas);
		}
	}

	@Override
	public void exportNlsMeanings() {
		final ChooseFileTranslationDialogResult result = viewFactory.exportFileTranslationDialog(controller.getView());
		if (result != null) {
			viewFactory.waitFullComponentViewWorker(controller.getView(), new AbstractLoadingViewWorker<File>() {
				@Override
				protected File doLoading() throws Exception {
					List<INlsMessageData> nlsMessageDatas = getNlsMessageDatas(result.getLocale());
					_exportExcel(result.getFile(), nlsMessageDatas);
					return result.getFile();
				}

				@Override
				public void success(File file) {
					try {
						SyDesktop.open(file);
					} catch (Exception e) {
					}
				}

				@Override
				public void fail(Throwable t) {
					viewFactory.showErrorMessageDialog(controller.getView(), t);
				}
			});
		}
	}

	private void _exportExcel(File file, List<INlsMessageData> nlsMessageDatas) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Feuille
		XSSFSheet tableSheet = workbook.createSheet(StaticCommonHelper.getCommonConstantsBundle().translation()); //$NON-NLS-1$

		_createExcelHeader(workbook, tableSheet);
		_createExcelData(workbook, tableSheet, nlsMessageDatas);

		for (int i = 0; i < COLUMN_HEADER.length; i++) {
			// tableSheet.autoSizeColumn(i);
			tableSheet.setColumnWidth(i, 20 * 256);
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			workbook.write(out);
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private void _createExcelHeader(XSSFWorkbook workbook, XSSFSheet tableSheet) throws Exception {
		XSSFFont f = workbook.createFont();
		f.setFontHeightInPoints((short) 12);
		f.setColor(IndexedColors.BLACK.getIndex());

		XSSFDataFormat df = workbook.createDataFormat();

		XSSFCellStyle cs = workbook.createCellStyle();
		cs.setFont(f);
		cs.setDataFormat(df.getFormat("text"));
		cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setWrapText(true);

		XSSFCreationHelper createHelper = workbook.getCreationHelper();

		XSSFRow rh = tableSheet.createRow(0);
		for (int i = 0; i < COLUMN_HEADER.length; i++) {
			XSSFCell c1 = rh.createCell(i);
			c1.setCellStyle(cs);
			c1.setCellValue(createHelper.createRichTextString(StaticCommonHelper.getNlsMessageDataTableConstantsBundle().getString(COLUMN_HEADER[i])));
			c1.setCellType(XSSFCell.CELL_TYPE_STRING);
		}
	}

	private void _createExcelData(XSSFWorkbook workbook, XSSFSheet tableSheet, List<INlsMessageData> nlsMessageDatas) throws Exception {
		if (nlsMessageDatas != null && !nlsMessageDatas.isEmpty()) {
			XSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 12);
			f.setColor(IndexedColors.BLACK.getIndex());

			XSSFDataFormat df = workbook.createDataFormat();

			XSSFCellStyle textCs = workbook.createCellStyle();
			textCs.setFont(f);
			textCs.setDataFormat(df.getFormat("text"));
			textCs.setWrapText(true);

			XSSFCreationHelper createHelper = workbook.getCreationHelper();

			for (int j = 0; j < nlsMessageDatas.size(); j++) {
				INlsMessageData nlsMessageData = nlsMessageDatas.get(j);
				int y = j + 1;

				XSSFRow rh = tableSheet.createRow(y);
				for (int i = 0; i < COLUMN_HEADER.length; i++) {
					XSSFCell cell = rh.createCell(i);

					Object d;
					if (i == 0) {
						if (componentClass != null) {
							d = nlsMessageData.getColumnName();
						} else {
							d = new StringBuilder().append(nlsMessageData.getObjectType().getName()).append("/").append(nlsMessageData.getColumnName());
						}
					} else {
						d = nlsMessageData.straightGetProperty(COLUMN_HEADER[i]);
					}

					if (d != null) {
						cell.setCellValue(createHelper.createRichTextString(d.toString()));
						cell.setCellStyle(textCs);
						cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					} else {
						cell.setCellValue(createHelper.createRichTextString(""));
						cell.setCellStyle(textCs);
						cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					}
				}
			}
		}
	}

	@Override
	public void importNlsMeanings() {
		ChooseFileTranslationDialogResult chooseFileTranslationDialogResult = viewFactory.importFileTranslationDialog(controller.getView());
		if (chooseFileTranslationDialogResult != null) {
			final File file = chooseFileTranslationDialogResult.getFile();
			final Locale locale = chooseFileTranslationDialogResult.getLocale();
			viewFactory.waitFullComponentViewWorker(controller.getView(), new AbstractLoadingViewWorker<XlsSheet[]>() {
				@Override
				protected XlsSheet[] doLoading() throws Exception {
					OPCPackage pkp = OPCPackage.open(file);
					try {
						XSSFWorkbook wb = new XSSFWorkbook(pkp);
						XlsSheet[] ss = new XlsSheet[wb.getNumberOfSheets()];
						for (int i = 0; i < wb.getNumberOfSheets(); i++) {
							XlsSheet s = new XlsSheet(i, wb.getSheetAt(i).getSheetName());
							ss[i] = s;

						}
						return ss;
					} finally {
						pkp.close();
					}
				}

				@Override
				public void success(XlsSheet[] ss) {
					if (ss != null && ss.length > 0) {
						importNlsMeanings(file, locale, ss);
					} else {
						viewFactory.showErrorMessageDialog(controller.getView(), StaticCommonHelper.getCommonConstantsBundle().importATranslationFile(), StaticCommonHelper.getCommonConstantsBundle()
								.theFileIsEmpty());
					}
				}

				@Override
				public void fail(Throwable t) {
					viewFactory.showErrorMessageDialog(controller.getView(), t);
				}
			});
		}
	}

	/*
	 * Choose param for excel and find existing translation and create union
	 */
	private void importNlsMeanings(final File file, final Locale locale, XlsSheet[] ss) {
		final ChooseParamImportExcelResult chooseParamImportExcelResult = viewFactory.chooseParamImportExcel(controller.getView(), true, ss);
		if (chooseParamImportExcelResult != null) {
			viewFactory.waitFullComponentViewWorker(controller.getView(), new AbstractLoadingViewWorker<List<INlsMessageData>>() {
				@Override
				protected List<INlsMessageData> doLoading() throws Exception {
					OPCPackage pkp = OPCPackage.open(file);
					try {
						XSSFWorkbook wb = new XSSFWorkbook(pkp);

						List<INlsMessageData> res = new ArrayList<INlsMessageData>();

						List<INlsMessageData> nlsMessageDatas = getNlsMessageDatas(locale);

						XlsSheet s = chooseParamImportExcelResult.getSheet();
						XSSFSheet selectedSheet = wb.getSheetAt(s.getIndex());

						int columnBundleName = chooseParamImportExcelResult.getColumnBundleName() - 1;
						int columnKey = chooseParamImportExcelResult.getColumnKey() - 1;
						int columnTraduction = chooseParamImportExcelResult.getColumnTraduction() - 1;
						int rowStart = chooseParamImportExcelResult.getRowStart() - 1;

						int nbTraductions = selectedSheet.getLastRowNum() - rowStart + 1;

						for (int i = 0; i < nbTraductions; i++) {
							XSSFRow row = selectedSheet.getRow(rowStart + i);

							Class<?> objectType;
							String columnName;
							if (componentClass != null) {
								objectType = componentClass;
								columnName = row.getCell(columnBundleName).getStringCellValue();
							} else {
								String[] ss = row.getCell(columnBundleName).getStringCellValue().split("/");
								objectType = DefaultNlsMessageExtendedContext.class.getClassLoader().loadClass(ss[0]);
								columnName = ss[1];
							}
							String key = row.getCell(columnKey).getStringCellValue();
							String traduction = row.getCell(columnTraduction).getStringCellValue();
							if (traduction != null && !traduction.trim().isEmpty()) {
								Serializable idObject = new IdRaw(key);

								INlsMessageData nlsMessageData = ComponentHelper.findComponentBy(nlsMessageDatas, NlsMessageDataFields.idObject().name(), idObject);

								INlsMessageData n = ComponentFactory.getInstance().createInstance(INlsMessageData.class);
								n.setObjectType(objectType);
								n.setColumnName(columnName);
								n.setIdObject(idObject);
								n.setMyMeaning(nlsMessageData != null ? nlsMessageData.getMyMeaning() : null);
								n.setMeaning(traduction);
								res.add(n);
							}
						}
						return res;
					} finally {
						pkp.close();
					}
				}

				@Override
				public void success(List<INlsMessageData> nlsClientMessages) {
					if (nlsClientMessages != null && !nlsClientMessages.isEmpty()) {
						importTraductionFile3(locale, nlsClientMessages);
					} else {
						viewFactory.showErrorMessageDialog(controller.getView(), StaticCommonHelper.getCommonConstantsBundle().importATranslationFile(), StaticCommonHelper.getCommonConstantsBundle()
								.theFileIsEmpty());
					}
				}

				@Override
				public void fail(Throwable t) {
					viewFactory.showErrorMessageDialog(controller.getView(), t);
				}
			});
		}
	}

	/*
	 * Show translation and save or update
	 */
	private void importTraductionFile3(final Locale locale, final List<INlsMessageData> nlsClientMessages) {
		if (viewFactory.showImportNlsMessageDatas(controller.getView(), nlsClientMessages)) {
			viewFactory.waitFullComponentViewWorker(controller.getView(), new AbstractSavingViewWorker<Boolean>() {
				@Override
				protected Boolean doSaving() throws Exception {
					setNlsMessageDatas(locale, nlsClientMessages);
					return true;
				}

				@Override
				public void success(Boolean e) {
					viewFactory.showInformationMessageDialog(controller.getView(), StaticCommonHelper.getCommonConstantsBundle().importATranslationFile(), StaticCommonHelper
							.getCommonConstantsBundle().success());
				}

				@Override
				public void fail(Throwable t) {
					viewFactory.showErrorMessageDialog(controller.getView(), t);
				}
			});
		}
	}
}
