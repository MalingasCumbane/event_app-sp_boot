package com.eventoapp.eventoapp.controllers;

import com.eventoapp.eventoapp.models.Convidado;
import com.eventoapp.eventoapp.models.Evento;
import com.eventoapp.eventoapp.repository.ConvidadoRepository;
import com.eventoapp.eventoapp.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EventoController {

    @Autowired
    private EventoRepository er;

    @Autowired
    private ConvidadoRepository cr;

    @RequestMapping(value = "/cadastrarEvento", method = RequestMethod.GET)
    public String form() {
        return "evento/formEvento";
    }

    @RequestMapping(value = "/cadastrarEvento", method = RequestMethod.POST)
    public String form(Evento evento) {

        er.save(evento);
        return "redirect:/cadastrarEvento";
    }

    @RequestMapping("/listarEventos")
    public ModelAndView listaEventos() {
        ModelAndView mv = new ModelAndView("evento/listarEventos");
        Iterable<Evento> eventos = er.findAll();
        mv.addObject("eventos", eventos);
        return mv;
    }

    @RequestMapping(value = "/{codigo}", method = RequestMethod.GET)
    public ModelAndView detalhesEvento(@PathVariable("codigo") long codigo) {
        Evento evento = er.findByCodigo(codigo);
        ModelAndView mv = new ModelAndView("evento/detalhesEvento");
        mv.addObject("evento", evento);

        Iterable<Convidado> convidados = cr.findByEvento(evento);

        mv.addObject("convidados", convidados);
        return mv;
    }

    @RequestMapping("/deletar")
    public String deletarEvento(long codigo) {
        Evento evento = er.findByCodigo(codigo);
        er.delete(evento);
        return "redirect:/listarEventos";
    }

    @RequestMapping("/deletarConvidado")
    public String deletarConvidado(long codigo) {
        Convidado conviado = cr.findByCodigo(codigo);
        cr.delete(conviado);

        Evento evento = conviado.getEvento();
        long codigoLong = evento.getCodigo();
        String codigoR = "" + codigoLong;
        return "redirect:/" + codigoR;
    }

    @RequestMapping(value = "/{codigo}", method = RequestMethod.POST)
    public String detalhesEventoComConvidado(@PathVariable("codigo") long codigo, @RequestParam("nomeConvidado") String nomeConvidado, @RequestParam("rg") String rg) {

        Evento evento = er.findByCodigo(codigo);

        Convidado convidado = new Convidado();
        convidado.setNomeConvidado(nomeConvidado);
        convidado.setRg(rg);
        convidado.setEvento(evento);

        cr.save(convidado);  // Agora o ID é Long, não String

        return "redirect:/" + codigo;  // Corrigir o redirect
    }
}
