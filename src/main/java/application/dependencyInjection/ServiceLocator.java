package application.dependencyInjection;

import application.Configuration;
import application.alerts.AlertFactory;
import application.controllers.MainController;
import application.controls.SidePanelControl;
import application.controls.screens.*;
import application.logging.ILogger;
import application.logging.Logger;
import application.controls.calendar.AppointmentLineControl;
import application.controls.calendar.CalendarControl;
import application.factories.*;
import application.services.*;
import dataAccess.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ServiceLocator {

    private static ServiceLocator instance;
    private static Map<Type, Supplier<Object>> map;
    private static Map<Type, IVarArgFunc> funcMap;

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        if (instance == null) {
            synchronized (ServiceLocator.class) {
                if (instance == null) {
                    instance = new ServiceLocator();
                    instance.map = new HashMap<>();
                    funcMap = new HashMap<>();
                    instance.createMappings();
                }
            }
        }
        return instance;
    }

    private void createMappings() {

        mapSingletons();

        mapRepos();
        mapContexts();

        mapFactories();

        mapControls();
        mapControlFuncs();

        //the last mapping needed
        map.put(MainController.class, () -> {

            LoginControl login = Resolve(LoginControl.class);
            SidePanelControl sidePanel = Resolve(SidePanelControl.class);
            IReminderService reminderService = Resolve(IReminderService.class);
            AlertFactory factory = Resolve(AlertFactory.class);
            Configuration config = Resolve(Configuration.class);

            return new MainController(login, sidePanel, reminderService, factory, config);
        });
    }

    private void mapSingletons() {
        Configuration configSingletonScope = new Configuration();
        IApplicationState stateSingletonScope = new ApplicationState();
        ILogger loggerSingletonScope = new Logger();
        IScheduler schedulerSingletonScope = new Scheduler();

        map.put(IApplicationState.class, () -> stateSingletonScope);
        map.put(Configuration.class, () -> configSingletonScope);
        map.put(ILogger.class, () -> loggerSingletonScope);
        map.put(ISqlRetryPolicy.class, () -> new SqlRetryPolicy(Resolve(Configuration.class), Resolve(ILogger.class)));
        map.put(IScheduler.class, () -> schedulerSingletonScope);
    }

    private void mapFactories() {
        map.put(DayControlFactory.class, () -> {
            IAppointmentLineControlFunc lineFunc = (IAppointmentLineControlFunc) ResolveFunc(AppointmentLineControl.class);
            IAppointmentControlFunc appControlFunc = (IAppointmentControlFunc) ResolveFunc(AppointmentControl.class);
            Configuration config = Resolve(Configuration.class);

            AppointmentControlFactory controlFactory = Resolve(AppointmentControlFactory.class);
            AppointmentLineControlFactory lineFactory = Resolve(AppointmentLineControlFactory.class);

            return new DayControlFactory(controlFactory, lineFactory, config);
        });
        map.put(WeekControlFactory.class, () -> {
            DayControlFactory factory = Resolve(DayControlFactory.class);
            return new WeekControlFactory(factory);
        });
        map.put(MonthControlFactory.class, () -> {
            WeekControlFactory factory = Resolve(WeekControlFactory.class);
            return new MonthControlFactory(factory);
        });

        map.put(AppointmentControlFactory.class, () -> {
            IAppointmentControlFunc appControlFunc = (IAppointmentControlFunc) ResolveFunc(AppointmentControl.class);
            IAppointmentContext context = Resolve(IAppointmentContext.class);
            IScheduler scheduler = Resolve(IScheduler.class);
            Configuration config = Resolve(Configuration.class);
            return new AppointmentControlFactory(context, scheduler, config, appControlFunc);
        });

        map.put(AppointmentLineControlFactory.class, () -> {

            IAppointmentLineControlFunc lineFunc = (IAppointmentLineControlFunc) ResolveFunc(AppointmentLineControl.class);
            return new AppointmentLineControlFactory(lineFunc);
        });

        map.put(AlertFactory.class, () -> {
            return new AlertFactory();
        });
    }

    private void mapControlFuncs() {

        funcMap.put(AppointmentControl.class, (IAppointmentControlFunc) args -> {
            IAppointmentContext context = Resolve(IAppointmentContext.class);
            IScheduler scheduler = Resolve(IScheduler.class);
            Configuration config = Resolve(Configuration.class);
            return new AppointmentControl(context, scheduler, config, args[0]);
        });

        funcMap.put(AppointmentLineControl.class, (IAppointmentLineControlFunc) args -> {
            Configuration config = Resolve(Configuration.class);
            AppointmentControlFactory factory = Resolve(AppointmentControlFactory.class);
            return new AppointmentLineControl(args[0], factory, config);
        });
    }

    private void mapControls() {
        map.put(LoginControl.class, () -> {
            IUserService service = Resolve(IUserService.class);
            ILogger logger = Resolve(ILogger.class);
            AlertFactory factory = Resolve(AlertFactory.class);
            return new LoginControl(service, factory, logger);
        });
        map.put(CalendarControl.class, () -> {
            ICalendarContext context = Resolve(ICalendarContext.class);
            IScheduler scheduler = Resolve(IScheduler.class);
            MonthControlFactory factory = Resolve(MonthControlFactory.class);
            IApplicationState state = Resolve(IApplicationState.class);
            return new CalendarControl(context, scheduler, factory, state);
        });
        map.put(CustomerControl.class, () -> {
            ICustomerContext service = Resolve(ICustomerContext.class);
            AlertFactory factory = Resolve(AlertFactory.class);
            return new CustomerControl(service, factory);
        });
        map.put(AppointmentControl.class, () -> {
            IAppointmentContext context = Resolve(IAppointmentContext.class);
            IScheduler scheduler = Resolve(IScheduler.class);
            Configuration config = Resolve(Configuration.class);
            return new AppointmentControl(context, scheduler, config);
        });

        map.put(ReportControl.class, () -> {
            IReportContext context = Resolve(IReportContext.class);
            IApplicationState state = Resolve(IApplicationState.class);
            return new ReportControl(context, state);
        });

        map.put(ReadmeControl.class, () -> {
            return new ReadmeControl();
        });

        map.put(SidePanelControl.class, () -> {
            CustomerControl customer = Resolve(CustomerControl.class);
            CalendarControl calendar = Resolve(CalendarControl.class);
            AppointmentControl appointment = Resolve(AppointmentControl.class);
            ReportControl report = Resolve(ReportControl.class);
            ReadmeControl readme = Resolve(ReadmeControl.class);
            return new SidePanelControl(customer, calendar, appointment, report, readme);
        });

        map.put(LoginControl.class, () -> {
            IUserService userService = Resolve(IUserService.class);
            AlertFactory factory = Resolve(AlertFactory.class);
            ILogger logger = Resolve(ILogger.class);
            return new LoginControl(userService, factory, logger);
        });
    }

    private void mapContexts() {
        map.put(IUserService.class, ()
                -> {
            IUserRepo repo = Resolve(IUserRepo.class);
            IApplicationState state = Resolve(IApplicationState.class);
            return new UserService(repo, state);
        });
        map.put(ICustomerContext.class, () -> {
            ICustomerRepo repo = Resolve(ICustomerRepo.class);
            IAddressRepo addressRepo = Resolve(IAddressRepo.class);
            ICityRepo cityRepo = Resolve(ICityRepo.class);
            ICountryRepo countryRepo = Resolve(ICountryRepo.class);
            return new CustomerContext(repo, addressRepo, cityRepo, countryRepo);
        });

        createSingletonReminderService();

        map.put(IAppointmentContext.class, () -> {
            ICustomerRepo customerRepo = Resolve(ICustomerRepo.class);
            IAppointmentRepo appointmentRepo = Resolve(IAppointmentRepo.class);
            IReminderRepo remRepo = Resolve(IReminderRepo.class);
            Configuration config = Resolve(Configuration.class);
            IApplicationState state = Resolve(IApplicationState.class);
            return new AppointmentContext(customerRepo, appointmentRepo, remRepo, state, config);
        });

        map.put(ICalendarContext.class, () -> {
            ICustomerRepo customerRepo = Resolve(ICustomerRepo.class);
            IAppointmentRepo appointmentRepo = Resolve(IAppointmentRepo.class);
            return new CalendarContext(customerRepo, appointmentRepo);
        });

        map.put(IReportContext.class, () -> {
            IAppointmentRepo appRepo = Resolve(IAppointmentRepo.class);
            return new ReportContext(appRepo);
        });
    }

    private void createSingletonReminderService() {
        Configuration config = Resolve(Configuration.class);
        IApplicationState state = Resolve(IApplicationState.class);
        IReminderRepo repo = Resolve(IReminderRepo.class);
        ILogger logger = Resolve(ILogger.class);
        IReminderService singletonScopedReminder = new ReminderService(repo, state, config, logger);
        map.put(IReminderService.class, () -> singletonScopedReminder);
    }

    private void mapRepos() {
        map.put(IUserRepo.class, () -> {
            IApplicationState state = Resolve(IApplicationState.class);
            Configuration config = Resolve(Configuration.class);
            ISqlRetryPolicy policy = Resolve(ISqlRetryPolicy.class);
            return new UserRepo(config, state, policy);
        });
        map.put(ICountryRepo.class, () -> {
            Configuration config = Resolve(Configuration.class);
            IApplicationState state = Resolve(IApplicationState.class);
            ISqlRetryPolicy policy = Resolve(ISqlRetryPolicy.class);
            return new CountryRepo(config, state, policy);
        });
        map.put(ICityRepo.class, () -> {
            Configuration config = Resolve(Configuration.class);
            IApplicationState state = Resolve(IApplicationState.class);
            ICountryRepo countryRepo = Resolve(ICountryRepo.class);
            ISqlRetryPolicy policy = Resolve(ISqlRetryPolicy.class);
            return new CityRepo(config, state, countryRepo, policy);

        });
        map.put(IAddressRepo.class, () -> {
            Configuration config = Resolve(Configuration.class);
            IApplicationState state = Resolve(IApplicationState.class);
            ICityRepo cityRepo = Resolve(ICityRepo.class);
            ISqlRetryPolicy policy = Resolve(ISqlRetryPolicy.class);
            return new AddressRepo(config, state, cityRepo, policy);
        });
        map.put(ICustomerRepo.class, () -> {
            Configuration config = Resolve(Configuration.class);
            IApplicationState state = Resolve(IApplicationState.class);
            IAddressRepo addressRepo = Resolve(IAddressRepo.class);
            ISqlRetryPolicy policy = Resolve(ISqlRetryPolicy.class);
            return new CustomerRepo(config, state, addressRepo, policy);
        });
        map.put(IAppointmentRepo.class, () -> {
            Configuration config = Resolve(Configuration.class);
            IApplicationState state = Resolve(IApplicationState.class);
            ICustomerRepo repo = Resolve(ICustomerRepo.class);
            ISqlRetryPolicy policy = Resolve(ISqlRetryPolicy.class);
            return new AppointmentRepo(config, state, repo, policy);
        });

        //circular dependency on apprepo -> remrepo
        map.put(IReminderRepo.class, () -> {
            Configuration config = Resolve(Configuration.class);
            IApplicationState state = Resolve(IApplicationState.class);
            ISqlRetryPolicy policy = Resolve(ISqlRetryPolicy.class);
            IAppointmentRepo appRepo = Resolve(IAppointmentRepo.class);
            return new ReminderRepo(config, state, null, appRepo, policy);
        });
    }

    public <T> T Resolve(Class<T> type) {

        Object mappedObject = null;
        if (map.containsKey(type)) {
            Supplier func = map.get(type);
            mappedObject = func.get();
        }
        try {
            return type.cast(mappedObject);
        } catch (Exception ex) {
            String exType = ex.getMessage();
            return null;
        }

    }

    public IVarArgFunc ResolveFunc(Type type) {

        IVarArgFunc func = funcMap.get(type);

        return func;
    }

}
