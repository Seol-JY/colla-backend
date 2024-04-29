package one.colla.common.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.stereotype.Component;

@Component
public class ApiDocumentationHelper {
	private static final String CONTENT_PREFIX = "content.";

	/**
	 * 성공 응답을 위한 REST API 문서화 필드 목록을 생성합니다.
	 * 이 메소드는 응답 코드, 선택적인 응답 내용(content), 그리고 응답 메시지를 포함한 기본 필드와,
	 * 사용자가 제공한 추가 필드를 결합하여 반환합니다.
	 * 추가 필드는 'content.' 접두사를 사용하여 {@code content} 객체 내에 위치하도록 자동으로 경로가 조정됩니다.
	 *
	 * @param additionalFields 사용자가 제공하는 추가적인 문서화 필드. 각 필드는 {@link FieldDescriptor} 객체로,
	 *                         'content.' 접두사가 붙은 경로로 자동 수정되어, 'content' 객체 내에 위치하게 됩니다.
	 *                         예를 들어, 필드 경로가 "name"으로 설정된 경우, "content.name"으로 경로가 조정됩니다.
	 * @return {@link FieldDescriptor} 배열을 반환합니다. 이 배열은 REST API 문서화에 사용될 모든 필드 목록을 포함하며,
	 *         기본 필드와 사용자가 제공한 추가 필드를 결합한 결과입니다.
	 */
	public FieldDescriptor[] createSuccessResponseFields(FieldDescriptor... additionalFields) {
		List<FieldDescriptor> commonFields = getFieldDescriptors(true, false);
		List<FieldDescriptor> modifiedAdditionalFields = Arrays.stream(additionalFields)
			.map(fd -> fieldWithPath(CONTENT_PREFIX + fd.getPath())
				.description(fd.getDescription())
				.type(fd.getType()))
			.toList();

		return Stream.concat(commonFields.stream(), modifiedAdditionalFields.stream())
			.toArray(FieldDescriptor[]::new);
	}

	/**
	 * 일반적인 실패 응답을 위한 REST API 문서화 필드 목록을 생성합니다.
	 * 이 메소드는 응답 코드와 응답 메시지를 포함합니다. 응답 내용(content)은 'null'로 설정됩니다.
	 *
	 * @return {@link FieldDescriptor} 배열을 반환합니다. 배열에는 'code', 'content', 'message' 필드가 포함되며,
	 *         'content' 필드는 null 값으로 설정되어 있고, 'message' 필드는 실패 사유를 설명하는 문자열입니다.
	 */
	public FieldDescriptor[] createErrorResponseFields() {
		List<FieldDescriptor> commonFields = getFieldDescriptors(false, true);
		return commonFields.toArray(FieldDescriptor[]::new);
	}

	private static List<FieldDescriptor> getFieldDescriptors(boolean hasContent, boolean hasMessage) {
		return List.of(
			fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
			fieldWithPath("content").description("응답 내용").type(hasContent ? JsonFieldType.OBJECT : JsonFieldType.NULL),
			fieldWithPath("message").description("응답 메시지").type(hasMessage ? JsonFieldType.STRING : JsonFieldType.NULL)
		);
	}
}
