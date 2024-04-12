package one.colla.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;

@Configuration
public class RestDocsConfiguration {

	@Bean
	public RestDocumentationResultHandler write() {
		return MockMvcRestDocumentationWrapper.document(
			"{class-name}/{method-name}",
			Preprocessors.preprocessRequest(
				Preprocessors.modifyHeaders()
					.remove("Content-Length")
					.remove("Host"),
				Preprocessors.prettyPrint()
			),
			Preprocessors.preprocessResponse(
				Preprocessors.modifyHeaders()
					.remove("Transfer-Encoding")
					.remove("Date")
					.remove("Keep")
					.remove("Connection"),
				Preprocessors.prettyPrint()
			)
		);
	}

}
