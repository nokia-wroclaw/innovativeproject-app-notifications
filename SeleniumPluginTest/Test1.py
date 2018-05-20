import time

from selenium import webdriver

# constants
visualise = True
visualiseTime = 0.5

extensionID = "gpogmlhmojicfobocfmpimkdeeignfeb"

# adding plugin to chrome
chrome_options = webdriver.ChromeOptions()
chrome_options.add_argument('--load-extension=D:\Studia\Semestr_06\Projekt\ChromePlugin')

# create a new Chrome session
driver = webdriver.Chrome(chrome_options=chrome_options)
driver.implicitly_wait(30)


# functions
def test_finish_message(message, code):
    print(message)
    driver.quit()
    SystemExit(code)
    return


def test_plugin():

    # move Chrome session to plugin
    driver.get('chrome-extension://'+extensionID+'/application/HTML/index.html')
    print("")
    print("Starting test")

    #
    # Try to login
    form_field = driver.find_element_by_name("login")
    form_field.send_keys("mwojcik")
    form_field = driver.find_element_by_name("password")
    form_field.send_keys("password1")

    if visualise:
        time.sleep(visualiseTime)

    form_field.submit()

    #
    # Get notifications list
    notification_list = driver.find_element_by_name("notificationList")
    if notification_list is None:
        test_finish_message("Logging on test account failure", 1)
        return

    if visualise:
        time.sleep(visualiseTime)

    #
    # Get notifications from list
    notifications = notification_list.find_elements_by_name("notification")
    if notifications is None:
        test_finish_message("test account don't have any notifications", 1)
        return

    #
    # "click" first notification
    notification_button = notifications[0].find_element_by_tag_name("h4")
    notification_button.click()

    if visualise:
        time.sleep(visualiseTime)

    if notifications[1] is None:
        test_finish_message("test account have only one notification", 1)
        return

    #
    # "click" second notification
    notification_button = notifications[1].find_element_by_tag_name("h4")
    notification_button.click()

    if visualise:
        time.sleep(visualiseTime)

    #
    # "click" last notification
    notification_button = notifications[notifications.__len__()-1].find_element_by_tag_name("h4")
    notification_button.click()

    if visualise:
        time.sleep(visualiseTime)

    #
    # "click" load more notifications
    load_notifications_button = driver.find_element_by_name("loadDataButton")
    load_notifications_button.click()

    #
    # Get notifications from list
    notifications2 = notification_list.find_elements_by_name("notification")
    if notifications.__len__() == notifications2.__len__():
        print("Loading more notification not working or not enough notifications on testing account")

    #
    # move to extensions
    services_button = driver.find_element_by_name("externalServices")
    if services_button is None:
        test_finish_message("fail to find external services button", 1)
        return
    services_button.click()

    if visualise:
        time.sleep(visualiseTime)

    #
    # move to twitter
    twitter_button = driver.find_element_by_name("twitterButton")
    if twitter_button is None:
        test_finish_message("fail to find twitter service button", 1)
        return
    twitter_button.click()

    if visualise:
        time.sleep(visualiseTime)

    #
    # move back to services list
    back_button = driver.find_element_by_name("backButton")
    if back_button is None:
        test_finish_message("fail to find back button from service window", 1)
        return
    back_button.click()

    if visualise:
        time.sleep(visualiseTime)

    #
    # move back to list
    back_button = driver.find_element_by_name("notfList")
    if back_button is None:
        test_finish_message("fail to find back button from services to notifications list", 1)
        return
    back_button.click()

    if visualise:
        time.sleep(visualiseTime)

    #
    # logout
    logout_button = driver.find_element_by_name("logoutButton")
    if back_button is None:
        test_finish_message("fail to find back logout button", 1)
        return
    logout_button.click()

    if visualise:
        time.sleep(visualiseTime)

    #
    # test competed
    test_finish_message("test competed success fully", 0)
    return


test_plugin()
