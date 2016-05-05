package gs.side.request;

import gs.side.CommandUserHelper;
import gs.side.request.impl.AbstractRequest;
import gs.side.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * Created by Dislike on 03.05.2016.
 */
public class RequestHolder {
    private static RequestHolder ourInstance = new RequestHolder();

    public static RequestHolder getInstance() {
        return ourInstance;
    }

    private final Map<String, AbstractRequest> pool;

    private RequestHolder() {
        pool = new HashMap<>();
        ReflectionUtil.getClassesForPackage(AbstractRequest.class.getPackage())
                .forEach(aClass -> {
                    Request requestAnnotation = aClass.getAnnotation(Request.class);
                    if (requestAnnotation != null) {
                        try {
                            pool.put(requestAnnotation.command(), (AbstractRequest) aClass.getConstructors()[0].newInstance());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
        CommandUserHelper.buildInformation(pool);

    }

    public Optional<AbstractRequest> get(String command) {
        AbstractRequest abstractRequest = null;
        StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
        if (stringTokenizer.hasMoreTokens())
            abstractRequest = pool.get(stringTokenizer.nextToken());
        return Optional.ofNullable(abstractRequest);
    }

}
