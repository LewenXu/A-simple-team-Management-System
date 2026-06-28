package web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import web.repository.ConnectionFactory;
import web.repository.InMemoryLeagueRepository;
import web.repository.JdbcLeagueRepository;
import web.repository.LeagueRepository;
import web.repository.RepositoryException;

public class ApplicationContextListener implements ServletContextListener {
    public static final String REPOSITORY_ATTRIBUTE = "leagueRepository";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        LeagueRepository repository = createRepository(context);
        repository.initialize();
        context.setAttribute(REPOSITORY_ATTRIBUTE, repository);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Object repository = event.getServletContext().getAttribute(REPOSITORY_ATTRIBUTE);
        if (repository instanceof LeagueRepository) {
            ((LeagueRepository) repository).close();
        }
    }

    private LeagueRepository createRepository(ServletContext context) {
        String jdbcUrl = context.getInitParameter("jdbc.url");
        if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
            context.setAttribute("persistenceMode", "memory");
            context.setAttribute("persistenceWarning", "No JDBC URL is configured; using in-memory data.");
            return new InMemoryLeagueRepository();
        }

        try {
            ConnectionFactory connectionFactory = new ConnectionFactory(
                    context.getInitParameter("jdbc.driver"),
                    jdbcUrl,
                    context.getInitParameter("jdbc.username"),
                    context.getInitParameter("jdbc.password"));
            LeagueRepository repository = new JdbcLeagueRepository(connectionFactory);
            repository.initialize();
            context.setAttribute("persistenceMode", "jdbc");
            return repository;
        } catch (RepositoryException e) {
            context.log("Falling back to in-memory repository because JDBC startup failed.", e);
            context.setAttribute("persistenceMode", "memory");
            context.setAttribute("persistenceWarning",
                    "JDBC startup failed; using in-memory data. Configure a JDBC driver before final submission.");
            return new InMemoryLeagueRepository();
        }
    }
}
