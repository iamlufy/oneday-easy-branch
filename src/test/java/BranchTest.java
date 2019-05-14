import com.oneday.easy.Branch;
import org.junit.Test;

/**
 * @author zhuangzf
 * @date 2019/5/13 18:36
 */
public class BranchTest {

    @Test
    public void test1() {
        String s = "123";
        Branch.of(s)
                .chain()
                .match(str -> str.contains("1")).ifTrue(str-> System.out.println("1"))
                .match(str -> str.contains("2")).ifTrue(str-> System.out.println("2"))
                .orElse(str -> System.out.println("123"));
    }

    @Test
    public void test2() {
        String s = "123";
        Branch.of(s)
                .whenIf()
                .match(str -> str.contains("1")).ifTrue(str -> System.out.println("1"))
                .match(str -> str.contains("2")).ifTrue(str -> System.out.println("2"))
                .orElseGet(() -> "123")
        ;
    }

    @Test
    public void test3() {
        String s = null;
        String ss = null;


        Branch.ofFast(1 == 1)
                .consumeTrue(() -> System.out.println("12312321"));
        Branch.ofFast(1 == 1)
                .thenThrow(NoSuchFieldError::new);

    }
}
