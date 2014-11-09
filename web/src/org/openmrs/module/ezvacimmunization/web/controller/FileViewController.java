package org.openmrs.module.ezvacimmunization.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ezvacimmunization.FileUploadBean;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.openmrs.web.WebUtil;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class FileViewController extends ParameterizableViewController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession httpSession = request.getSession();
		String success = "";
		String error = "";
		MessageSourceAccessor msa = getMessageSourceAccessor();
		InputStream inputStream = null;
		String filename = null;
		InputStream in = null;
		OutputStream output = null;

		String action = ServletRequestUtils.getStringParameter(request,
				"action", "");
		int formDataLength = request.getContentLength();
		FileUploadBean bean = new FileUploadBean();
		byte dataBytes[] = bean.getData();
		int c = 0;
		if ("upload".equals(action)
				&& request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultipartFile multipartUploadFile = multipartRequest
					.getFile("uploadFile");
			if (multipartUploadFile != null && !multipartUploadFile.isEmpty()) {
				filename = WebUtil.stripFilename(multipartUploadFile
						.getOriginalFilename());

				try {

					inputStream = multipartUploadFile.getInputStream();
					output = new FileOutputStream(OpenmrsUtil
							.getApplicationDataDirectory()
							+ File.separator + filename);
					in = inputStream;
					while (((c = in.read(dataBytes, 0, 1024)) != -1)) {
						formDataLength += c;
						output.write(dataBytes, 0, c);
					}
					success = msa.getMessage("ezvacimmunization.loaded",
							new String[] { filename });
				} finally {
					try {

						if (in != null)
							in.close();
						if (output != null)
							output.close();
					} catch (IOException io) {
						log.warn("Unable to close temporary input stream", io);
					}
				}
			} 
		}
		if (!success.equals(""))
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, success);

		if (!error.equals(""))
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, error);

		return new ModelAndView(getViewName());

	}
}
