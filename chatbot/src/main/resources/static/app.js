(() => {
    const transcript = document.getElementById("transcript");
    const emptyState = document.getElementById("empty-state");
    const form = document.getElementById("composer");
    const input = document.getElementById("message");
    const sendButton = document.getElementById("send");
    const toolsList = document.getElementById("tools-list");
    const toolsStatus = document.getElementById("tools-status");
    const toolsPanel = document.getElementById("tools-panel");
    const toolsToggle = document.getElementById("tools-toggle");

    const storage = {
        get(area, key) {
            try {
                return window[area].getItem(key);
            } catch {
                return null;
            }
        },
        set(area, key, value) {
            try {
                window[area].setItem(key, value);
            } catch {
            }
        }
    };

    const newId = () => (crypto.randomUUID ? crypto.randomUUID() : String(Date.now()) + Math.random().toString(16).slice(2));

    let conversationId = storage.get("sessionStorage", "conversationId") || newId();
    storage.set("sessionStorage", "conversationId", conversationId);
    let busy = false;

    const renderMarkdown = (text) => DOMPurify.sanitize(marked.parse(text || "", { gfm: true, breaks: true }));

    const scrollToBottom = () => {
        transcript.scrollTop = transcript.scrollHeight;
    };

    const element = (tag, className, text) => {
        const node = document.createElement(tag);
        if (className) {
            node.className = className;
        }
        if (text !== undefined) {
            node.textContent = text;
        }
        return node;
    };

    const prettyArguments = (raw) => {
        try {
            return JSON.stringify(JSON.parse(raw), null, 2);
        } catch {
            return raw || "";
        }
    };

    const addUserMessage = (text) => {
        emptyState.hidden = true;
        const message = element("div", "message user");
        message.append(element("div", "bubble", text));
        transcript.append(message);
        scrollToBottom();
    };

    const addAssistantMessage = (reply, toolCalls) => {
        const message = element("div", "message assistant");
        if (toolCalls && toolCalls.length > 0) {
            const calls = element("div", "tool-calls");
            toolCalls.forEach((call) => {
                const details = element("details", call.failed ? "tool-call failed" : "tool-call");
                const summary = element("summary", null, (call.failed ? "✕ " : "⚙ ") + call.name);
                details.append(summary, element("pre", null, prettyArguments(call.arguments)));
                calls.append(details);
            });
            message.append(calls);
        }
        const bubble = element("div", "bubble");
        bubble.innerHTML = renderMarkdown(reply);
        message.append(bubble);
        transcript.append(message);
        scrollToBottom();
    };

    const addError = (text, retryMessage) => {
        const message = element("div", "message assistant error");
        message.append(element("div", "bubble", text));
        if (retryMessage) {
            const retry = element("button", "btn btn-ghost retry", "Try again");
            retry.type = "button";
            retry.addEventListener("click", () => {
                message.remove();
                send(retryMessage, false);
            });
            message.append(retry);
        }
        transcript.append(message);
        scrollToBottom();
    };

    const showTyping = () => {
        const message = element("div", "message assistant");
        message.id = "typing";
        const bubble = element("div", "bubble typing");
        bubble.setAttribute("aria-label", "Assistant is typing");
        bubble.append(element("span"), element("span"), element("span"));
        message.append(bubble);
        transcript.append(message);
        scrollToBottom();
    };

    const hideTyping = () => document.getElementById("typing")?.remove();

    const setBusy = (value) => {
        busy = value;
        sendButton.disabled = value;
        input.setAttribute("aria-busy", String(value));
    };

    const send = async (text, echo = true) => {
        const message = text.trim();
        if (!message || busy) {
            return;
        }
        if (echo) {
            addUserMessage(message);
        }
        setBusy(true);
        showTyping();
        try {
            const response = await fetch("/api/chat", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ conversationId, message })
            });
            const body = await response.json().catch(() => ({}));
            hideTyping();
            if (!response.ok) {
                addError(body.detail || "Something went wrong. Please try again.", message);
                return;
            }
            addAssistantMessage(body.reply, body.toolCalls);
        } catch {
            hideTyping();
            addError("Could not reach the assistant. Check your connection and try again.", message);
        } finally {
            setBusy(false);
            input.focus();
        }
    };

    const autosize = () => {
        input.style.height = "auto";
        input.style.height = Math.min(input.scrollHeight, 180) + "px";
        input.style.overflowY = input.scrollHeight > 180 ? "auto" : "hidden";
    };

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        const text = input.value;
        input.value = "";
        autosize();
        send(text);
    });

    input.addEventListener("keydown", (event) => {
        if (event.key === "Enter" && !event.shiftKey && !event.isComposing) {
            event.preventDefault();
            form.requestSubmit();
        }
    });

    input.addEventListener("input", autosize);

    document.querySelectorAll(".suggestion").forEach((button) => {
        button.addEventListener("click", () => send(button.textContent));
    });

    document.getElementById("new-chat").addEventListener("click", async () => {
        if (busy) {
            return;
        }
        fetch("/api/chat/" + encodeURIComponent(conversationId), { method: "DELETE" }).catch(() => {});
        conversationId = newId();
        storage.set("sessionStorage", "conversationId", conversationId);
        transcript.querySelectorAll(".message").forEach((node) => node.remove());
        emptyState.hidden = false;
        input.focus();
    });

    const root = document.documentElement;
    const savedTheme = storage.get("localStorage", "theme");
    if (savedTheme === "light" || savedTheme === "dark") {
        root.dataset.theme = savedTheme;
    }
    document.getElementById("theme-toggle").addEventListener("click", () => {
        const current = root.dataset.theme || (matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light");
        const next = current === "dark" ? "light" : "dark";
        root.dataset.theme = next;
        storage.set("localStorage", "theme", next);
    });

    toolsToggle.addEventListener("click", () => {
        const open = toolsPanel.classList.toggle("open");
        toolsToggle.setAttribute("aria-expanded", String(open));
    });

    const loadTools = async () => {
        try {
            const response = await fetch("/api/tools");
            if (!response.ok) {
                throw new Error(String(response.status));
            }
            const tools = await response.json();
            toolsList.replaceChildren(...tools.map((tool) => {
                const item = element("li");
                item.append(element("div", "tool-name", tool.name), element("p", "tool-description", tool.description));
                item.title = tool.description;
                return item;
            }));
            toolsStatus.textContent = tools.length + " tools";
            toolsStatus.className = "badge ok";
        } catch {
            toolsStatus.textContent = "Unavailable";
            toolsStatus.className = "badge down";
        }
    };

    loadTools();
    input.focus();
})();
