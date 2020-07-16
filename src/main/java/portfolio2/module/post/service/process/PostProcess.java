package portfolio2.module.post.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.post.dto.PostRequestDto;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class PostProcess {

    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public Post saveNewPost(Account sessionAccount, PostRequestDto postRequestDto){
        Account authorAccountInDb = accountRepository.findByUserId(sessionAccount.getUserId());
        Post newPost = new Post();
        newPost.setAuthor(authorAccountInDb);
        newPost.setTitle(postRequestDto.getTitle());
        newPost.setContent(postRequestDto.getContent());
        LocalDateTime currentTime = LocalDateTime.now();
        newPost.setFirstWrittenTime(currentTime);
        newPost.setLastModifiedTime(currentTime);
        return postRepository.save(newPost);
    }

    public Post addTagToNewPost(Post savedNewPostInDb, PostRequestDto postRequestDto){
        // 태그 처리
        if (!postRequestDto.getTagTitleOnPost().isEmpty()){
            String[] tagArray = postRequestDto.getTagTitleOnPost().split(",");
            for(String tagTitle : tagArray){
                Tag newTagOnNewPost;
                Tag existingTagInDb = tagRepository.findByTitle(tagTitle);
                if(existingTagInDb == null){
                    // 해당 타이틀로 기존에 데이터베이스에 존재하는 태그가 없으면,
                    newTagOnNewPost = new Tag();
                    newTagOnNewPost.setTitle(tagTitle);
                    newTagOnNewPost = tagRepository.save(newTagOnNewPost);
                }else{
                    newTagOnNewPost = existingTagInDb;
                }
                savedNewPostInDb.getTag().add(newTagOnNewPost);
            }
        }
        return savedNewPostInDb;
    }
}