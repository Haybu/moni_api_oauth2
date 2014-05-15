package com.moni.provider.web;

/**
 * Created by hmohamed on 5/13/14.
 */

import com.moni.provider.model.Greeting;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Controller
@RequestMapping("/api")
public class GreetingController {

    private static final Logger logger = Logger.getLogger(GreetingController.class.getName());

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/greeting" , method = RequestMethod.GET, produces="application/json")
    public @ResponseBody Greeting greeting(
            @RequestParam(value="name", required=false, defaultValue="World") String name)
    {
        logger.info("calling greeting API end-point");
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
}
