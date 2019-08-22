package com.mazen.document.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.mazen.document.entities.Document;
import com.mazen.document.repos.DocumentRepository;

@Controller
public class DocumentController {

	@Autowired
	DocumentRepository documentRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

	@RequestMapping("/displayUpload")
	public String displayUpload(ModelMap modelMap) {

		LOGGER.info("inside displayUpload()");

		retreiveExistedDocuments(modelMap);
		return "documentUpload";
	}

	private void retreiveExistedDocuments(ModelMap modelMap) {
		List<Document> documents = documentRepository.findAll();
		modelMap.addAttribute("documents", documents);
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uploadDocument(@RequestParam("document") MultipartFile multiPartFile, @RequestParam("id") long id,
			ModelMap modelMap) {

		LOGGER.info("inside uploadDocument()");

		Document document = new Document();
		document.setId(id);
		document.setName(multiPartFile.getOriginalFilename());
		try {
			document.setData(multiPartFile.getBytes());
		} catch (IOException e) {
			// logging the exception into log file
			LOGGER.info("Exception happened while uploading : " + e.getMessage());
//			e.printStackTrace();
		}
		documentRepository.save(document);
		retreiveExistedDocuments(modelMap);
		return "documentUpload";

	}

	@RequestMapping("/download")
	public StreamingResponseBody download(@RequestParam("id") Long id, HttpServletResponse response) {

		Document document = documentRepository.findById(id).orElse(null);
		byte[] data = document.getData();
		response.setHeader("Content-Disposition",
				"attachment;filename=downloaded.jpeg".replace("downloaded", document.getName()));
		return outputStream -> {
			outputStream.write(data);
		};

	}

}
