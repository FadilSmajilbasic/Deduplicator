package samt.smajilbasic.deduplicator.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer
{
    @Override
    protected Class<?>[] getRootConfigClasses()
    {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses()
    {
        return new Class[] {SecurityConfig.class};
    }

    @Override
    protected String[] getServletMappings()
    {
        return new String[] {"/"};
    }
}