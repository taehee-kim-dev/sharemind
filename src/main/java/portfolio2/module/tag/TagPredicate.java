package portfolio2.module.tag;

import com.querydsl.core.types.Predicate;
import portfolio2.module.account.QAccount;

import java.util.List;
import java.util.Set;

public class TagPredicate {

    public static Predicate findAllTagByAccountInterestTagAndPostTag(List<Tag> accountInterestTag, List<Tag> postTag){
        QTag tag = QTag.tag;
        return tag.in(accountInterestTag).and(tag.in(postTag));
    }
}
