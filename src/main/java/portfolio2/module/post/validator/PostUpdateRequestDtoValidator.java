package portfolio2.module.post.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import portfolio2.module.post.dto.PostUpdateRequestDto;

@Component
@RequiredArgsConstructor
public class PostUpdateRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PostUpdateRequestDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PostUpdateRequestDto postUpdateRequestDto = (PostUpdateRequestDto)target;

        if(postUpdateRequestDto.getTitle().length() == 0){
            errors.rejectValue("title", "emptyTitle","제목을 작성해 주세요.");
        }
    }
}