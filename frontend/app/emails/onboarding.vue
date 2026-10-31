<script setup lang="ts">
import brandCss from "#layers/base/app/assets/css/base.css?inline"
</script>

<template>
	<EHtml class="dark" style="background-color: var(--ui-bg)">
		<EPreview v-html="`Welcome, {{ displayName }}. Please verify your Account to get started.`"/>

		<EHead>
			<EStyle>{{ brandCss }}</EStyle>
		</EHead>
		<EBody style="color: var(--ui-text); font-family: var(--font-sans, Arial)">
			<EContainer style="background-color: color-mix(in oklab, var(--ui-bg-muted) 50%, transparent); border: var(--ui-border) 1px solid; border-radius: 8px; margin: 100px auto;">
				<ESection style="padding: 20px; border-bottom: var(--ui-border) 1px solid">
					<EText style="font-size: 20px; margin: 0">
						Snoty
					</EText>
				</ESection>

				<ESection style="padding: 30px 20px; border-bottom: var(--ui-border) 1px solid">
					<EHeading as="h1" style="margin: 0;">
						Welcome aboard! 🎉
					</EHeading>
					<EText style="margin-top: 5px; font-size: 18px">
						Welcome to Snoty, <b v-html="`{{ displayName }}`"/>.
					</EText>

					<EText style="font-size: 18px;">
						Please verify your e-mail and complete your onboarding.
					</EText>

					<div style="text-align: center; margin: 40px">
						<EButton href="{{ onboardingUrl }}" style="background-color: var(--color-brand-500); color: var(--ui-text-inverted); margin: 0 auto; padding: 10px 20px; border-radius: 6px">
							Finish Onboarding  ➡
						</EButton>
					</div>

					<EText style="margin-bottom: 0">
						If the Button doesn't work, copy and paste this link into your browser:
					</EText>
					<ELink href="{{ onboardingUrl }}" style="color: var(--color-brand-500); word-wrap: anywhere" v-html="`{{ onboardingUrl }}`"/>
				</ESection>

				<ESection style="padding: 20px">
					<EText style="margin: 0">
						This message was sent by Snoty.
					</EText>
					<EText style="margin: 0">
						If you did not sign up, please ignore this e-mail.
					</EText>
				</ESection>
			</EContainer>
		</EBody>
	</EHtml>
</template>
